# 🍊 KotlinAPI – Produits alimentaires, Scanner & Boissons recommandées

Application Android développée en **Kotlin** avec **Jetpack Compose**, permettant de :

- 🔎 Rechercher des **produits alimentaires** depuis l’API **OpenFoodFacts**
- ⭐ Gérer des **produits favoris** (persistance Room)
- 🥤 Obtenir une **boisson recommandée** grâce à **TheCocktailDB**
- 📸 Scanner un **QR Code ou code-barres** pour retrouver un produit instantanément
- 🧪 Visualiser une **analyse nutritionnelle complète**
- 🅰️ Filtrer par **Nutri-Score**
- 🎨 Profiter d'une UI moderne **orange / jaune**, fluide et responsive

---

## 📸 Aperçu des fonctionnalités

---

## 🏠 Écran d’accueil (Home)

- Champ de recherche connecté à OpenFoodFacts  
- Résultats avec **image + nom + Nutri-Score**  
- Filtre Nutri-Score (A→E)  
- **Scanner un produit** : code-barres & QR grâce à ZXing  
- CTA : **Découvrir un produit** (aléatoire)

---

## 📄 Écran détail produit

Pour chaque produit sélectionné :

- 📷 Image
- 🏷 Nom, catégories, origine
- 🧾 Ingrédients
- 🅰️ Nutri-Score codé couleur
- 🧪 Analyse nutritionnelle :
  - calories
  - sucres
  - graisses
  - sel
  - additifs
- ⭐ Ajouter / retirer des favoris  
- 🥤 **Boisson recommandée** → basée sur le premier ingrédient

---

## ⭐ Écran favoris

- 🎠 Carousel horizontal (swiper)
- 🖼 Grande image du produit
- 🔎 Bouton "Voir le produit"
- 📋 Liste complète
- 🗑 Supprimer un favori
- Message si vide :
  > "Aucun produit pour l'instant. Ajoutez des favoris depuis les fiches produits."

---

## 🥤 Écran boisson recommandée

Fonctionnalités :

- Nettoyage intelligent des ingrédients  
- Mapping **FR → EN**  
- Requête TheCocktailDB  
- Sélection **ALÉATOIRE** d’une boisson (variation à chaque fois)  
- Affichage : image, nom, instructions  
- Gestion erreur : loading + message si aucune boisson trouvée

---

## 📡 Scanner un produit

- Intégration ZXing (`journeyapps.barcodescanner`)
- Support de :
  - QR Code
  - EAN-13, EAN-8
  - UPC, Code128
  - et bien plus
- Le scan redirige automatiquement vers :
detail/{code}/Produit scanné

yaml
Copier le code

---

## 🏗️ Architecture du projet

### 📂 Structure

data/
RecipeApiClient.kt
DrinkApiClient.kt
model/
Recipe.kt
NutritionInfo.kt
Drink.kt
database/
AppDatabase.kt
FavoriteRecipeDao.kt
repository/
RecipeRepository.kt
DrinkInfoRepository.kt
presentation/
viewmodels/
RecipeViewModel.kt
FavoriteRecipeViewModel.kt
DrinkInfoViewModel.kt
screen/
HomeScreen.kt
DetailScreen.kt
FavoritesScreen.kt
DrinkInfoScreen.kt

yaml
Copier le code

### 🧠 MVVM + Flow

- ViewModel pour la logique
- StateFlow pour les états UI
- Coroutines + Ktor pour les appels réseau
- Room pour le stockage
- Navigation Compose pour les écrans
- Coil pour le chargement d’images

---

## 🌐 APIs utilisées

### 🔎 OpenFoodFacts

- Recherche :
https://fr.openfoodfacts.org/cgi/search.pl?search_terms=...&json=1&page_size=20

bash
Copier le code
- Détail :
https://fr.openfoodfacts.org/api/v2/product/{code}.json

shell
Copier le code

### 🍹 TheCocktailDB

https://www.thecocktaildb.com/api/json/v1/1/filter.php?i={ingredient}

yaml
Copier le code

Logique appliquée :

- Nettoyage texte
- Mapping intelligent FR→EN
- Sélection aléatoire

---

## 🧰 Stack technique

- Kotlin
- Jetpack Compose (Material 3)
- Ktor HTTP client
- kotlinx.serialization & JSONObject
- Room (DAO + Entity + DB)
- Navigation Compose
- ZXing Scanner
- Coil
- MVVM

---

## 🚀 Installation & lancement

### 1. Prérequis
- Android Studio (Koala ou +)
- JDK 11
- Min SDK 24

### 2. Cloner le projet

```bash
git clone https://github.com/ton-compte/ton-projet.git
cd ton-projet
3. Installer les dépendances
Lancer une synchronisation Gradle dans Android Studio.

4. Lancer sur un appareil ou émulateur
🧭 Navigation
Routes disponibles :

arduino
Copier le code
home
detail/{id}/{name}
favorites
drinkInfo/{ingredient}
🎯 Améliorations futures
Historique des scans

Mode sombre

Comparateur nutritionnel

Suggestions intelligentes

Liste de courses générée automatiquement

Badge “santé” calculé par l’algorithme

