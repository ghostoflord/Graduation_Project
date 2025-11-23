# Java Spring Starter template
This template is used for the Java Spring Restful series (Capstone Project) by the author Ghost.

## Author
Yo, What's up, bro?.I'm Ghost.Details about the Ghost author are available on GitHub https://github.com/ghostoflord.

## Version
JDK 17

## Railway deployment

The repository already contains a production-ready `Dockerfile`, `.dockerignore`, and `railway.json`. Follow the steps below to ship the app to Railway.

### 1. Prerequisites
- [Railway](https://railway.app) account with the CLI installed (`npm i -g @railway/cli`).
- MySQL instance (Railway has a 1-click MySQL plugin) and the secrets you plan to inject.
- (Optional) A Railway volume for persistent uploads if you need to keep user files.

### 2. Bootstrap the project on Railway
```bash
railway login
railway init --service backend
# or link an existing service
railway link
```

The CLI will detect the `Dockerfile` automatically. You can run `railway up --service backend` to trigger the first build/deploy pipeline.

### 3. Configure environment variables
Map your secrets to the properties that were parameterized in `application.properties`. The essential ones are listed here:

| Purpose | Railway variable | Example |
| --- | --- | --- |
| JDBC URL | `SPRING_DATASOURCE_URL` | `jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}?useSSL=false&serverTimezone=UTC` |
| DB user | `SPRING_DATASOURCE_USERNAME` | `${MYSQLUSER}` |
| DB password | `SPRING_DATASOURCE_PASSWORD` | `${MYSQLPASSWORD}` |
| JWT secret | `GHOST_JWT_SECRET` | `base64-encoded-secret` |
| Mail credentials | `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` | Gmail app password |
| OAuth | `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` | provider values |
| VNPay | `VNPAY_TMN_CODE`, `VNPAY_HASH_SECRET`, `VNPAY_RETURN_URL` | provider values |
| File storage | `UPLOAD_BASE_URI`, `UPLOAD_AVATAR_DIR`, `UPLOAD_PRODUCT_DIR`, `UPLOAD_SLIDE_DIR` | e.g. `file:///data/upload/` |

If you attach Railway's MySQL plugin, it automatically adds `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, and `MYSQLPASSWORD`. Use those to craft the JDBC string shown above.

### 4. Persist uploaded files
If you need to keep avatars/products/slides between deploys:
1. Create a volume: `railway volume create uploads --mountPath /data/upload`.
2. Set `UPLOAD_BASE_URI=file:///data/upload/`.
3. Set the `UPLOAD_*` directory variables to point inside `/data/upload`.

### 5. Deploy
Every time you push changes:
```bash
railway up --service backend
```
Railway will build the Docker image, run the container, and expose the HTTP port that Spring Boot picks up through the `PORT` variable (already wired in `application.properties`).