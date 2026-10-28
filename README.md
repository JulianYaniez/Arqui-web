Using Package By Feature Architecture

Catalog uses Mongo

Ticketing will use Redis for queue

Run "docker compose up -d" on root to raise the whole project

Use "docker compose up -d [db_type]-[service]" to run specific database for local dev.

Run "docker compose down" to stop the containers. Can be general or targeted. Use flag -v for destroy.
