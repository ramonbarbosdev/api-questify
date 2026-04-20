# 🚀 Vertex AI (Gemini) — Setup Simples (Dev & Prod)

Guia direto pra fazer o **Spring Boot + Spring AI (Gemini)** funcionar sem erro de credencial.

---

##  Regra única

O Google precisa da variável:

```
GOOGLE_APPLICATION_CREDENTIALS
```

Sem isso, você vai ver:

```
Your default credentials were not found
```

---

#  DEV (local)

##  PowerShell (Windows)

```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\Users\Ramon\Documents\dev\Java\api-questify\gcp-credentials.json"
mvn spring-boot:run
```

Verificar:

```powershell
echo $env:GOOGLE_APPLICATION_CREDENTIALS
```

---

## Debug no VSCode

```json
{
  "type": "java",
  "name": "Debug Spring Boot",
  "request": "launch",
  "mainClass": "com.api_questify.ApiQuestifyApplication",
  "env": {
    "GOOGLE_APPLICATION_CREDENTIALS": "C:\\Users\\Ramon\\Documents\\dev\\Java\\api-questify\\gcp-credentials.json"
  }
}
```

---

##  Teste

```java
chatClient.prompt()
    .user("Say OK")
    .call()
    .content();
```

Esperado:

```
OK
```

---

#  PROD (Linux)

##  Estrutura

```
/home/app/
 ├── gcp-credentials.json
```

---

## Segurança

```bash
chmod 600 /home/app/gcp-credentials.json
```

---

# PROD (Docker) ✅ **IMPORTANTE**

Em Docker, **export NÃO funciona**

Você precisa:

## 1. Passar variável

```bash
-e GOOGLE_APPLICATION_CREDENTIALS=/app/gcp-credentials.json
```

---

## 2. Montar o arquivo (VOLUME)

```bash
-v /home/app/gcp-credentials.json:/app/gcp-credentials.json
```

---

## Exemplo completo

```bash
docker run -d \
  --name api-questify \
  -p 8082:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e GOOGLE_APPLICATION_CREDENTIALS=/app/gcp-credentials.json \
  -v /home/app/gcp-credentials.json:/app/gcp-credentials.json \
  ghcr.io/seu-repo:latest
```

---

##  Por que isso é necessário?

Dentro do container:

* `/home/app/...` NÃO existe 
* `/app/gcp-credentials.json` existe 

---

## Testar dentro do container

```bash
docker exec -it api-questify sh
```

```bash
echo $GOOGLE_APPLICATION_CREDENTIALS
ls -l /app/gcp-credentials.json
```

---

# Config do Spring

```properties
spring.ai.vertex.ai.gemini.project-id=SEU_PROJECT_ID
spring.ai.vertex.ai.gemini.location=us-central1
spring.ai.vertex.ai.gemini.model=gemini-1.5-flash
```

---

# O que NÃO funciona

* `.env` sozinho
* `application.properties` com credencial
* `export` fora do container (Docker)
* caminho que não existe dentro do container

---

# Insight importante

```
Host ≠ Container
```

Você precisa garantir:

```
ENV + arquivo acessível dentro do container
```

---

# Conclusão

✔️ Defina `GOOGLE_APPLICATION_CREDENTIALS`
✔️ Use caminho válido dentro do container
✔️ Monte o JSON com volume (`-v`)
✔️ Rode a aplicação

---

## Resultado final

Sua aplicação estará pronta para:

* gerar conteúdo com IA
* rodar em produção
* executar jobs automáticos
* escalar com segurança

---
