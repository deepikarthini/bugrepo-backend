# PowerShell script to delete Rishi user from PostgreSQL database
# Make sure PostgreSQL is running and psql is in your PATH

# Database connection details (update if different)
$DB_HOST = "localhost"
$DB_PORT = "5432"
$DB_NAME = "bugtracker"
$DB_USER = "postgres"
$DB_PASSWORD = "8989"

Write-Host "Deleting Rishi user from database..." -ForegroundColor Yellow

# Set password environment variable
$env:PGPASSWORD = $DB_PASSWORD

# Execute SQL command
$sql = "DELETE FROM users WHERE email LIKE '%rishi%' OR full_name LIKE '%Rishi%'; SELECT COUNT(*) as remaining_users FROM users;"

psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c $sql

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nRishi user deleted successfully!" -ForegroundColor Green
} else {
    Write-Host "`nFailed to delete user. Make sure PostgreSQL is running." -ForegroundColor Red
}

# Clear password from environment
Remove-Item Env:\PGPASSWORD
