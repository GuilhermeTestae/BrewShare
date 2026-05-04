BrewShare 
> Micro rede social para amantes de café — compartilhe seu café, sua história e sua cidade.
---
 Demonstração (vídeo curto)
  https://youtube.com/shorts/J8f36u5sduE?feature=share

 Explicação do Código (vídeo longo)
  https://youtu.be/Uys1dYX5tro

 Sobre o App
O BrewShare é uma micro rede social temática voltada para apreciadores de café. Os usuários podem publicar fotos e descrições dos seus cafés favoritos, com marcação automática da cidade onde estão. O feed exibe as postagens de toda a comunidade com paginação por cursor, e é possível buscar posts por cidade.
---
 Requisitos Funcionais Implementados
RF	Descrição	Status
RF1-1	Tela de login com logo, e-mail, senha, botão login e criar conta	✅
RF1-2	Tela de cadastro com e-mail, senha e confirmação	✅
RF1-3	Autenticação com Firebase Authentication (e-mail/senha)	✅
RF1-4	Redirecionamento automático para Home se já logado	✅
RF2-1	Criar post com imagem (galeria), texto e cidade (GPS automático)	✅
RF2-2	Posts salvos no Firebase Firestore com metadados	✅
RF3-1	Feed paginado (5 por vez, scroll infinito com cursor)	✅
RF3-2	Busca de posts por nome da cidade	✅
RF3-3	Edição de nome completo, foto de perfil e senha	✅
RF4-1	Geolocalização com Geocoder — coordenadas → nome da cidade	✅
---
 Arquitetura e Pacotes
```
br.com.brewshare/
├── adapter/        # PostAdapter (RecyclerView)
├── auth/           # AuthHelper (Firebase Authentication)
├── dao/            # UserDAO, PostDAO (Firebase Firestore)
├── location/       # LocalizacaoHelper (FusedLocation + Geocoder)
├── model/          # Post.kt, User.kt (data classes)
├── ui/             # Activities (Login, Register, Profile, Home, CreatePost, EditProfile)
└── util/           # Base64Converter
```
As Activities não acessam o Firebase diretamente — elas chamam as classes de cada pacote, mantendo o código desacoplado e organizado.
---
 Estrutura do Firestore
```
usuarios (coleção)
└── email@exemplo.com (documento)
    ├── email: "email@exemplo.com"
    ├── username: "barista_joao"
    ├── nomeCompleto: "João Silva"
    └── fotoPerfil: "//base64..."

posts (coleção)
└── {id_aleatorio} (documento)
    ├── imageString: "//base64..."
    ├── descricao: "Espresso perfeito hoje!"
    ├── cidade: "Araraquara"
    ├── autor: "email@exemplo.com"
    ├── autorNome: "barista_joao"
    ├── autorFoto: "//base64..."
    └── timestamp: 1714000000000
```
---
 Tema Visual
Paleta de cores inspirada no café:
Cor	Hex	Uso
Marrom escuro	`#4A2C2A`	Cor primária, botões, header
Marrom médio	`#7B4F3A`	Acentos e ícones
Dourado/caramelo	`#D4A87A`	Detalhes e bordas
Creme	`#FDF6EC`	Fundo das telas
---
 Tecnologias Utilizadas
Kotlin — linguagem principal
Firebase Authentication — autenticação de usuários
Firebase Firestore — banco de dados NoSQL em nuvem
FusedLocationProvider — geolocalização
Geocoder — geocodificação reversa (coordenadas → cidade)
RecyclerView com paginação por cursor (timestamp)
ViewBinding — referência de views sem findViewById
Base64Converter — armazenamento de imagens como string
Material Design — componentes visuais
---
 Como Configurar e Rodar
1. Criar projeto no Firebase
Acesse console.firebase.google.com
Crie um novo projeto chamado `BrewShare`
Adicione um app Android com o pacote `br.com.brewshare`
Baixe o arquivo `google-services.json`
2. Ativar serviços no Firebase
Authentication → Método de login → Ativar E-mail/senha
Firestore → Criar banco → Modo de teste
3. Configurar o projeto
Substitua `app/google-services.json` pelo arquivo baixado
Abra o projeto no Android Studio
Aguarde a sincronização do Gradle
Rode o app em um dispositivo com API 33+
4. Criar índice no Firestore
No console do Firebase → Firestore → Índices → Adicionar índice composto:
Coleção	Campo	Direção
posts	cidade	Crescente
posts	timestamp	Decrescente
---
 Observações
As imagens são convertidas para Base64 e armazenadas no Firestore, sem necessidade de cartão de crédito para o Firebase Storage
A paginação usa cursor temporal (timestamp do último post), técnica mais eficiente que offset para feeds dinâmicos
O scroll infinito detecta quando o usuário chega perto do final da lista e carrega mais 5 posts automaticamente
---
 Desenvolvido por
Guilherme — IFSP Campus Araraquara — Análise e Desenvolvimento de Sistemas  
Disciplina: ARQDMO2 - Dispositivos Móveis 2  
Professor: Henrique Galati
