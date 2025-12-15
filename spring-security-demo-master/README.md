# 🔐 Spring Security Demo - Gestion des Rôles et OAuth2

Application de démonstration Spring Boot illustrant l'authentification multi-méthodes (formulaire + OAuth2/OIDC) avec gestion des rôles et permissions granulaires.

## 📋 Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Technologies](#-technologies)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [Structure du projet](#-structure-du-projet)
- [Sécurité](#-sécurité)

## ✨ Fonctionnalités

### Authentification Multi-Méthodes
- **Formulaire classique** : Username/Password avec BCrypt
- **OAuth2/OIDC** : Connexion avec Google
- **Redirection intelligente** : Selon le rôle après login

### Gestion des Rôles

#### 👑 ADMIN
- Gestion complète des utilisateurs (CRUD)
- Modification des rôles
- Dashboard avec statistiques système
- Suppression de toutes les tâches
- Accès à toutes les fonctionnalités

#### 👔 GÉRANT (Manager)
- Création et assignation de tâches
- Réassignation de tâches
- Modification du statut de toutes les tâches
- Vue globale de tous les projets

#### 👤 USER
- Vue de ses propres tâches uniquement
- Modification du statut de ses tâches
- Marquage des tâches comme complétées

### Interface Utilisateur
- **Dashboard Admin** : Statistiques (utilisateurs, tâches)
- **Gestion des tâches** : Modal de création, filtres par statut/assignation
- **Gestion des utilisateurs** : Édition inline des rôles
- **Navigation contextuelle** : Liens adaptés selon le rôle
- **Design moderne** : Bootstrap 5 avec icônes

## 🏗️ Architecture

### Flux d'Authentification

```
┌─────────────────────────────────────────────────────────────┐
│                    Page de Login                            │
│  • Formulaire (username/password)                           │
│  • Bouton "Sign in with Google"                             │
└─────────────────┬───────────────────────────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
        ▼                   ▼
┌───────────────┐   ┌──────────────────┐
│ Form Login    │   │ OAuth2/OIDC      │
│               │   │ (Google)         │
└───────┬───────┘   └────────┬─────────┘
        │                    │
        ▼                    ▼
┌───────────────┐   ┌──────────────────┐
│ UserService   │   │ OAuth2UserService│
│ (Database)    │   │ (Google API)     │
└───────┬───────┘   └────────┬─────────┘
        │                    │
        └──────────┬─────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │ CustomUserDetails    │
        │ (UserDetails +       │
        │  OAuth2User)         │
        └──────────┬───────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │ Success Handler      │
        │ • ADMIN → Dashboard  │
        │ • GÉRANT → Tasks     │
        │ • USER → Tasks       │
        └──────────────────────┘
```

### Composants Principaux

#### Configuration
- **`SecurityConfig`** : Configuration Spring Security (routes, providers)
- **`AppConfig`** : Beans globaux (PasswordEncoder)
- **`DotenvConfig`** : Chargement des variables d'environnement

#### Authentification
- **`CustomUserDetails`** : Implémente `UserDetails` + `OAuth2User`
- **`CustomOAuth2UserService`** : Gestion des utilisateurs OAuth2
- **`CustomAuthenticationSuccessHandler`** : Redirection post-login

#### Modèles
- **`User`** : Entité utilisateur (username, password, role, provider)
- **`Task`** : Entité tâche (title, description, status, assignedTo)
- **`Role`** : Enum (ROLE_ADMIN, ROLE_GERANT, ROLE_USER)
- **`Status`** : Enum (TODO, IN_PROGRESS, COMPLETED, CANCELED)

## 🛠️ Technologies

- **Java 21**
- **Spring Boot 3.x**
- **Spring Security 6**
- **Spring Data JPA**
- **H2 Database** (développement)
- **MySQL** (production - optionnel)
- **Thymeleaf** (templates)
- **Bootstrap 5** (UI)
- **Lombok** (réduction boilerplate)
- **Dotenv Java** (gestion variables d'environnement)

## 📦 Installation

### Prérequis
- Java 21+
- Maven 3.8+
- (Optionnel) MySQL 8+

### Étapes

1. **Cloner le projet**
```bash
git clone https://github.com/sats0264/spring-security-demo.git
cd spring-security-demo
```

2. **Créer le fichier `.env`**
```bash
# Copier le template
cp .env.example .env
```

3. **Configurer les credentials Google OAuth2**

Créez un projet sur [Google Cloud Console](https://console.cloud.google.com/) :
- Activez l'API Google+
- Créez des identifiants OAuth 2.0
- URI de redirection : `http://localhost:8080/login/oauth2/code/google`

Puis ajoutez dans `.env` :
```env
GOOGLE_CLIENT_ID=votre-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=votre-client-secret
```

4. **Lancer l'application**
```bash
mvn spring-boot:run
```

5. **Accéder à l'application**
- URL : http://localhost:8080
- Console H2 : http://localhost:8080/h2-console

## ⚙️ Configuration

### Base de données

#### H2 (par défaut - développement)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
```

#### MySQL (production)
Décommentez dans `application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spring_security_demo
spring.datasource.username=root
spring.datasource.password=votre-password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

### OAuth2 / OIDC

Configuration dans `application.properties` :
```properties
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=profile,email
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
```

### Utilisateurs par défaut

L'application initialise automatiquement 3 utilisateurs :

| Username | Password | Rôle     |
|----------|----------|----------|
| admin    | password | ADMIN    |
| gerant   | password | GÉRANT   |
| user     | password | USER     |

## 🚀 Utilisation

### Connexion

#### Formulaire
1. Accédez à http://localhost:8080
2. Utilisez un des comptes par défaut (ex: `admin` / `password`)
3. Vous serez redirigé selon votre rôle

#### Google OAuth2
1. Cliquez sur "Sign in with Google"
2. Authentifiez-vous avec votre compte Google
3. Un compte USER sera créé automatiquement avec votre email

### Fonctionnalités par rôle

#### ADMIN
- **Dashboard** : `/admin/dashboard` - Statistiques système
- **Gestion utilisateurs** : `/admin/users` - CRUD complet
- **Gestion tâches** : `/tasks` - Vue complète + suppression

#### GÉRANT
- **Gestion tâches** : `/tasks` - Création, réassignation, modification statut

#### USER
- **Mes tâches** : `/tasks` - Vue personnelle, modification statut

### Gestion des tâches

1. **Créer une tâche** (ADMIN/GÉRANT)
   - Cliquez sur "Create New Task"
   - Remplissez le formulaire (titre, description, assignation)

2. **Filtrer les tâches**
   - Par statut : TODO, IN_PROGRESS, COMPLETED, CANCELED
   - Par assignation : Sélectionnez un utilisateur

3. **Modifier le statut**
   - Cliquez sur "Start" (TODO → IN_PROGRESS)
   - Cliquez sur "Complete" (→ COMPLETED)

4. **Réassigner** (ADMIN/GÉRANT)
   - Sélectionnez un utilisateur dans le dropdown "Reassign"

## 📁 Structure du projet

```
spring-security-demo/
├── src/main/java/com/misi2/springsecuritydemo/
│   ├── config/
│   │   ├── AppConfig.java                    # Beans globaux (PasswordEncoder)
│   │   ├── SecurityConfig.java               # Configuration Spring Security
│   │   ├── DotenvConfig.java                 # Chargement .env
│   │   ├── CustomUserDetails.java            # UserDetails + OAuth2User
│   │   ├── CustomOAuth2UserService.java      # Service OAuth2
│   │   ├── CustomAuthenticationSuccessHandler.java  # Redirection post-login
│   │   └── DataInitializer.java              # Données initiales
│   ├── controller/
│   │   ├── AdminController.java              # Routes admin
│   │   ├── TaskController.java               # Routes tâches
│   │   └── HomeController.java               # Routes publiques
│   ├── model/
│   │   ├── User.java                         # Entité utilisateur
│   │   ├── Task.java                         # Entité tâche
│   │   ├── Role.java                         # Enum rôles
│   │   └── Status.java                       # Enum statuts
│   ├── repository/
│   │   ├── UserRepository.java               # DAO utilisateurs
│   │   └── TaskRepository.java               # DAO tâches
│   └── service/
│       ├── UserService.java                  # Service utilisateurs
│       ├── TaskService.java                  # Service tâches
│       └── MyUserDetailsService.java         # UserDetailsService
├── src/main/resources/
│   ├── templates/
│   │   ├── fragments/
│   │   │   └── layout.html                   # Layout commun (navbar, etc.)
│   │   ├── admin/
│   │   │   ├── dashboard.html                # Dashboard admin
│   │   │   └── users.html                    # Gestion utilisateurs
│   │   ├── tasks/
│   │   │   └── list.html                     # Liste des tâches
│   │   ├── home.html                         # Page d'accueil
│   │   └── login.html                        # Page de login
│   ├── application.properties                # Configuration Spring
│   └── META-INF/
│       └── spring.factories                  # Enregistrement DotenvConfig
├── .env                                      # Variables d'environnement (non versionné)
├── .gitignore                                # Fichiers ignorés par Git
├── pom.xml                                   # Dépendances Maven
└── README.md                                 # Ce fichier
```

## 🔒 Sécurité

### Bonnes pratiques implémentées

✅ **Mots de passe hashés** : BCrypt avec salt automatique  
✅ **Variables d'environnement** : Credentials dans `.env` (non versionné)  
✅ **Protection CSRF** : Désactivée pour démo (à réactiver en production)  
✅ **Validation OAuth2** : Tokens validés par Spring Security  
✅ **Autorisation granulaire** : `@PreAuthorize` sur les endpoints  
✅ **Session management** : Gérée par Spring Security  

### Points d'attention pour la production

⚠️ **Réactiver CSRF** : Modifier `SecurityConfig.java`
```java
http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
```

## 📝 Licence

Ce projet est à des fins de démonstration et d'apprentissage.

## 🤝 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à ouvrir une issue ou une pull request.

---

**Développé avec 😃 pour démontrer Spring Security**
