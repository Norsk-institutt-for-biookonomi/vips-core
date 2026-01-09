# VIPSCore Docker Setup

1. Edit `models_for_docker.xml` and add/remove model dependencies as needed.

2. **Start VIPSCore:**
   ```bash
   docker compose up --build -d
   ```
   (Or `docker-compose` if you're on an older version of Docker compose)

3. **Example endpoints:**
   - **http://localhost:8080/VIPSCore/models/json**
   - **http://localhost:8080/VIPSCore/models/ALTERNARIA/usage**

