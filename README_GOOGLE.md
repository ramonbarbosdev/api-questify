# Configuração do Google Vertex AI (Gemini) no Spring Boot

Este guia explica como configurar o acesso ao **Google Vertex AI (Gemini)** em uma aplicação Spring Boot usando **Service Account (JSON)**.

---

## Pré-requisitos

Antes de começar, você precisa:

* Um projeto no Google Cloud
* Vertex AI API ativada
* Billing ativo no projeto

---

## 1. Gerar Service Account (JSON)

### 1.1 Acesse o console do Google Cloud

 https://console.cloud.google.com/

---

### 1.2 Vá para Contas de Serviço

Menu:

```
IAM e administrador → Contas de serviço
```

Ou direto:

```
https://console.cloud.google.com/iam-admin/serviceaccounts
```

---

### 1.3 Selecionar ou criar conta

Você pode:

* Usar uma conta existente
  ou
* Criar uma nova (ex: `spring-ai-service`)

---

### 1.4 Gerar nova chave

1. Clique na conta
2. Vá na aba **"Chaves"**
3. Clique em:

```
Adicionar chave → Criar nova chave
```

4. Escolha:

```
JSON
```

 O arquivo será baixado automaticamente

---

## Importante (Segurança)

* ❌ NÃO subir o JSON no GitHub
* ❌ NÃO compartilhar o arquivo
* ✔️ Adicionar no `.gitignore`

Exemplo:

```
*.json
```

---

## 2. Permissões necessárias

A conta de serviço deve ter:

```
Vertex AI User
```

Ou (para testes):

```
Vertex AI Administrator
```

---

## 3. Configurar no Spring Boot

### ✔️ Opção recomendada

No `application.properties`:

```
spring.cloud.gcp.credentials.location=file:C:/caminho/para/seu-arquivo.json

spring.ai.vertex.ai.gemini.project-id=SEU_PROJECT_ID
spring.ai.vertex.ai.gemini.location=us-central1
spring.ai.vertex.ai.gemini.model=gemini-1.5-flash
```

---

## 4. Região recomendada

Use:

```
us-central1
```

 Região mais estável para modelos Gemini

---

## 5. Teste de funcionamento

Faça um teste simples:

```java
String response = chatClient.prompt()
        .user("Responda apenas OK")
        .call()
        .content();

System.out.println(response);
```

### Resultado esperado:

```
OK
```

---

## Problemas comuns

### "Failed to generate content"

Possíveis causas:

* Credencial não configurada
* Permissão insuficiente
* API não ativada
* Região incorreta
* Billing desativado

---

### JSON não encontrado

Verifique:

```
spring.cloud.gcp.credentials.location
```

---

### Permissão negada

Adicione role:

```
Vertex AI User
```

---

## Observações

* O Service Account funciona como a identidade da aplicação no GCP
* Sem ele, o Gemini não responde
* Você pode gerar novas chaves a qualquer momento

---

## Boas práticas

* Remover chaves antigas não utilizadas
* Nunca versionar credenciais
* Usar variáveis de ambiente em produção

---

## Conclusão

Após essa configuração, sua aplicação Spring Boot estará pronta para:

* Gerar conteúdo com IA
* Integrar com Gemini
* Construir features como desafios diários

---

## Próximos passos

* Criar service de geração de desafios
* Persistir no banco
* Expor endpoint `/desafio/hoje`

---
