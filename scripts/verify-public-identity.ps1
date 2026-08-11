[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$blockedName = -join ([char[]](69, 116, 104, 97, 110))
$failures = [System.Collections.Generic.List[string]]::new()

function Add-Failure {
    param([string]$Category, [string[]]$Lines)

    foreach ($line in $Lines) {
        if ($line) {
            $failures.Add("${Category}: $line")
        }
    }
}

function Invoke-GitScan {
    param([string]$Category, [string[]]$Arguments)

    $output = & git @Arguments 2>&1
    if ($LASTEXITCODE -notin 0, 1) {
        throw "git $($Arguments -join ' ') failed with exit code $LASTEXITCODE"
    }

    $matches = @($output | Select-String -SimpleMatch -Pattern $blockedName -CaseSensitive:$false | ForEach-Object Line)
    Add-Failure -Category $Category -Lines $matches
}

Invoke-GitScan -Category 'tracked content' -Arguments @('grep', '-I', '-n', '-i', '-e', $blockedName, '--', '.')
Invoke-GitScan -Category 'tracked path' -Arguments @('ls-files')

$untrackedFiles = @(& git ls-files --others --exclude-standard)
if ($LASTEXITCODE -ne 0) {
    throw 'Unable to list untracked files.'
}
Add-Failure -Category 'untracked path' -Lines @(
    $untrackedFiles |
        Select-String -SimpleMatch -Pattern $blockedName -CaseSensitive:$false |
        ForEach-Object Line
)
foreach ($untrackedFile in $untrackedFiles) {
    $file = Get-Item -LiteralPath $untrackedFile -ErrorAction Stop
    if ($file.Length -gt 10MB) {
        continue
    }

    $content = [System.IO.File]::ReadAllText($file.FullName)
    if ($content.Contains($blockedName, [System.StringComparison]::OrdinalIgnoreCase)) {
        Add-Failure -Category 'untracked content' -Lines @($untrackedFile)
    }
}

& git rev-parse --verify HEAD *> $null
if ($LASTEXITCODE -eq 0) {
    Invoke-GitScan -Category 'history metadata' -Arguments @('log', '--all', '--format=%H%x09%an%x09%ae%x09%cn%x09%ce%x09%s%x09%b')
    Invoke-GitScan -Category 'history path' -Arguments @('log', '--all', '--name-only', '--format=')
}
Invoke-GitScan -Category 'ref name' -Arguments @('for-each-ref', '--format=%(refname)', 'refs/heads', 'refs/remotes', 'refs/tags')

Add-Type -AssemblyName System.IO.Compression.FileSystem
$trackedArchives = @(& git ls-files '*.jar' '*.zip')
if ($LASTEXITCODE -ne 0) {
    throw 'Unable to list tracked archives.'
}
$builtArchives = @(Get-ChildItem -Path 'build/libs' -File -ErrorAction SilentlyContinue |
        Where-Object Extension -In '.jar', '.zip' |
        ForEach-Object FullName)
$archives = @($trackedArchives) + @($builtArchives)

foreach ($archive in $archives) {
    $resolvedArchive = if ([System.IO.Path]::IsPathRooted($archive)) {
        $archive
    }
    else {
        Join-Path (Get-Location) $archive
    }
    $zip = [System.IO.Compression.ZipFile]::OpenRead($resolvedArchive)
    try {
        foreach ($entry in $zip.Entries) {
            if ($entry.FullName.Contains($blockedName, [System.StringComparison]::OrdinalIgnoreCase)) {
                Add-Failure -Category 'archive path' -Lines @("$archive::$($entry.FullName)")
            }

            if ($entry.Length -eq 0 -or $entry.Length -gt 10MB) {
                continue
            }

            $stream = $entry.Open()
            try {
                $reader = [System.IO.StreamReader]::new($stream, [System.Text.Encoding]::UTF8, $true)
                try {
                    $content = $reader.ReadToEnd()
                }
                finally {
                    $reader.Dispose()
                }
            }
            finally {
                $stream.Dispose()
            }

            if ($content.Contains($blockedName, [System.StringComparison]::OrdinalIgnoreCase)) {
                Add-Failure -Category 'archive content' -Lines @("$archive::$($entry.FullName)")
            }
        }
    }
    finally {
        $zip.Dispose()
    }
}

if ($failures.Count -gt 0) {
    Write-Error "Public identity verification failed:`n$($failures -join [Environment]::NewLine)"
    exit 1
}

Write-Host 'Public identity verification passed.'
