[CmdletBinding()]
param(
    [string]$PrismInstancesRoot = (Join-Path $env:APPDATA 'PrismLauncher\instances')
)

$ErrorActionPreference = 'Stop'

if (-not (Test-Path -LiteralPath $PrismInstancesRoot)) {
    throw "Prism instances directory was not found: $PrismInstancesRoot"
}

$targetsByHash = @{}
foreach ($instance in Get-ChildItem -LiteralPath $PrismInstancesRoot -Directory) {
    $configurationPath = Join-Path $instance.FullName 'instance.cfg'
    $displayName = ''
    if (Test-Path -LiteralPath $configurationPath) {
        $displayName = (Select-String -LiteralPath $configurationPath -Pattern '^name=' |
                Select-Object -First 1).Line -replace '^name=', ''
    }

    if ($instance.Name -notmatch 'CMA' -and $displayName -notmatch 'CMA') {
        continue
    }

    $activeJars = @(Get-ChildItem -LiteralPath $instance.FullName -Recurse -File -Filter 'the_vault*.jar' -ErrorAction SilentlyContinue)
    foreach ($jar in $activeJars) {
        $hash = (Get-FileHash -LiteralPath $jar.FullName -Algorithm SHA256).Hash
        if (-not $targetsByHash.ContainsKey($hash)) {
            $targetsByHash[$hash] = [PSCustomObject]@{
                Instance = $displayName
                Jar = $jar
                Hash = $hash
            }
        }
    }
}

if ($targetsByHash.Count -eq 0) {
    throw 'No active The Vault jars were found in CMA-labelled Prism instances.'
}

$results = [System.Collections.Generic.List[object]]::new()
foreach ($target in $targetsByHash.Values | Sort-Object { $_.Jar.Name }) {
    Write-Host "Compiling against $($target.Jar.Name) from $($target.Instance)..."
    & .\gradlew.bat clean compileJava --no-daemon "-Pvault_mod_jar=$($target.Jar.FullName)"
    $passed = $LASTEXITCODE -eq 0
    $results.Add([PSCustomObject]@{
        Instance = $target.Instance
        Jar = $target.Jar.Name
        SHA256 = $target.Hash
        Result = if ($passed) { 'PASS' } else { 'FAIL' }
    })
    if (-not $passed) {
        $results | Format-Table -AutoSize
        exit $LASTEXITCODE
    }
}

$results | Format-Table -AutoSize
