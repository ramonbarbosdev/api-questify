# Vertex AI (Gemini) — Setup Simples (Dev & Prod)

Guia direto pra fazer o **Spring Boot + Spring AI (Gemini)** funcionar sem erro de credencial.

---

## Regra única

O Google precisa da variável:

```
GOOGLE_APPLICATION_CREDENTIALS
```

Sem isso, você vai ver:

```
Your default credentials were not found
```

---

# DEV (local)

##  PowerShell (Windows)

```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\Users\Ramon\Documents\dev\Java\api-questify\gcp-credentials.json"
mvn spring-boot:run
```

```powershell
echo $env:GOOGLE_APPLICATION_CREDENTIALS
```

para iniciar com o debug:

```powershell
Run and Debug → create launch.json → Java

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

esperado:

```
OK
```

---

#  PROD (servidor Linux)

##  Definir variável

```bash
export GOOGLE_APPLICATION_CREDENTIALS=/home/app/gcp-credentials.json
```

---

## Estrutura

```
/home/app/
 ├── gcp-credentials.json
```

---

##  Segurança

```bash
chmod 600 /home/app/gcp-credentials.json
```

---

# Config do Spring

```properties
spring.ai.vertex.ai.gemini.project-id=SEU_PROJECT_ID
spring.ai.vertex.ai.gemini.location=us-central1
spring.ai.vertex.ai.gemini.model=gemini-1.5-flash
```

---

#  O que NÃO funciona

* `.env` sozinho
* `application.properties` com credencial
* carregar JSON via código (com starter)

---

#  Conclusão

* Defina `GOOGLE_APPLICATION_CREDENTIALS`
* Use caminho absoluto
* Rode a aplicação

acabou o problema

---
