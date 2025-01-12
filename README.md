# Application JavaCard - Terminal Bancaire 💳

Ce projet JavaCard implémente une application de terminal bancaire avec une gestion de l'authentification par code PIN et permet de réaliser des opérations bancaires de base telles que **créditer**, **débiter** et **consulter le solde**. L'applet JavaCard est conçue pour être utilisée sur des cartes à puce JavaCard pour assurer la sécurité des transactions.

## Fonctionnalités

- **Authentification par code PIN** : L'utilisateur doit entrer un code PIN pour accéder aux fonctionnalités bancaires.
- **Opérations bancaires de base** :
  - **Créditer un compte** : Ajouter de l'argent à un solde.
  - **Débiter un compte** : Retirer de l'argent du solde.
  - **Consulter le solde** : Afficher le montant actuellement disponible sur le compte.
- **Sécurité** : La carte est protégée par un code PIN et les opérations ne peuvent être effectuées qu'après une authentification réussie.

## Technologies utilisées

- **JavaCard** : Plateforme pour développer des applications sécurisées sur des cartes à puce.
- **Java** : Langage de programmation utilisé pour développer l'applet.
- **APDU** (Application Protocol Data Units) : Utilisé pour la communication entre la carte et le lecteur.

## Installation

### Prérequis

- **JavaCard SDK** : Vous devez avoir le JavaCard SDK installé pour développer et tester l'applet.
- **Eclipse ou un autre IDE Java** : Un environnement de développement pour coder et déployer l'applet.

### Étapes d'installation

1. **Clonez le projet**
   ```bash
   git clone https://github.com/Islem-Chammakhi/JavaCard-project.git
   cd java-card-terminal-bancaire
