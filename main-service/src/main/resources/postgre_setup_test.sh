docker run --name ewm_db-postgres \
-e POSTGRES_USER=dbuser \
-e POSTGRES_PASSWORD=12345 \
-e POSTGRES_DB=ewm_db \
-p 5432:5432 \
-v shareit-data:/tmp/postgresql/java_explore_with_me_plus/data \
-d postgres