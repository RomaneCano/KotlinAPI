# 🍊 KotlinAPI – Produits alimentaires & boissons recommandées

Application Android développée en **Kotlin** avec **Jetpack Compose**, permettant de :

- 🔎 Rechercher des **produits alimentaires** depuis l’API **OpenFoodFacts**
- ⭐ Gérer une liste de **produits favoris** (persistés en local avec Room)
- 🥤 Obtenir une **boisson recommandée** pour un produit, grâce à l’API **TheCocktailDB**
- 📱 Naviguer entre un **écran d’accueil**, un **détail produit**, un **écran favoris** et un **écran boisson**

---

## 📸 Aperçu des fonctionnalités

### 🏠 Écran d’accueil (Home)

- Champ de recherche : filtrage des produits via **OpenFoodFacts**
- Liste de résultats :
  - Affichage du **nom** et de la **photo** du produit
  - Clic sur un produit → ouverture de la **fiche détaillée**
- Bouton CTA (Call To Action) en bas de l’écran :
  - **« Découvrir un produit »** → charge un produit **aléatoire** via OpenFoodFacts

### 📄 Écran détail produit

Pour chaque produit sélectionné :

- Image du produit (thumbnail)
- Nom, catégorie, pays d’origine
- Liste des **ingrédients**
- Bouton **« + favoris / - favoris »**
  - Permet d’ajouter ou retirer le produit de la base locale Room
- Bouton **« Boisson recommandée »** :
  - Utilise le premier ingrédient (ou un fallback) pour interroger l’API **TheCocktailDB**
  - Navigation vers l’écran **DrinkInfo**

### ⭐ Écran favoris

- Fond dégradé **jaune/orange**
- **Carousel** en haut :
  - Swipe horizontal entre les produits favoris
  - Grande image + nom
  - Bouton **« Voir le produit »** pour revenir à l’écran détail
- Liste des favoris en dessous :
  - Affichage compact : image + nom
  - Bouton **poubelle** pour supprimer un favori
- Message stylé lorsque la liste est vide :
  - _"Aucun produit pour l'instant. Ajoutez des favoris depuis les fiches produits."_

### 🥤 Écran boisson recommandée

Pour un ingrédient donné :

- Appel à l’API **TheCocktailDB**
- Nettoyage de l’ingrédient (suppression des accents, parenthèses, etc.)
- **Mapping intelligent** FR → EN (et quelques alias) pour matcher les ingrédients de la base TheCocktailDB
- Sélection **aléatoire** d’une boisson parmi celles disponibles pour l’ingrédient
- Affichage :
  - Image du cocktail
  - Nom de la boisson
  - Texte du type _« Boisson contenant : [ingrédient] »_
- Gestion des erreurs :
  - Spinner de chargement
  - Message si aucune boisson n’est trouvée ou en cas d’erreur API

---

## 🏗️ Architecture & organisation

L’application suit une structure simple en plusieurs couches :

- **data/**
  - Clients API Ktor (`RecipeApiClient`, `DrinkApiClient`)
  - Accès réseau : OpenFoodFacts, TheCocktailDB
- **model/**
  - `Recipe` : modèle pour les produits alimentaires
  - `Drink` : modèle pour les boissons
  - Entités Room pour les favoris (`FavoriteRecipeEntity`)
- **repository/**
  - `RecipeRepository` : encapsule la logique d’accès OpenFoodFacts
  - `DrinkInfoRepository` : encapsule l’accès à l’API TheCocktailDB
- **presentation/**
  - `RecipeViewModel` : gestion de la liste & du détail des produits
  - `FavoriteRecipeViewModel` : gestion des favoris
  - `DrinkInfoViewModel` : gestion de l’appel boisson, état UI (loading / succès / erreur)
- **presentation/screen/**
  - `HomeScreen`
  - `DetailScreen`
  - `FavoritesScreen`
  - `DrinkInfoScreen`
- **database/**
  - `AppDatabase`, `FavoriteRecipeDao` : persistance locale avec Room

Navigation gérée avec **Navigation Compose** :

- Route `home`
- Route `detail/{id}/{name}`
- Route `favorites`
- Route `drinkInfo/{ingredient}`

---

## 🧰 Stack technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose + Material 3
- **Navigation** : `androidx.navigation:navigation-compose`
- **HTTP Client** : Ktor (engine CIO)
- **JSON** :
  - `kotlinx.serialization` (config Ktor)
  - `org.json.JSONObject` pour certains parsings manuels
- **Image loading** : Coil Compose
- **Persistence locale** : Room (DAO + Entity + Database)
- **Architecture** :
  - MVVM simplifié
  - `StateFlow` pour exposer l’état UI (chargement, erreurs, données)
  - `viewModelScope` + coroutines pour les appels réseau

---

## 🌐 APIs utilisées

### 🔎 OpenFoodFacts

- **Base de données ouverte** sur les produits alimentaires
- Endpoints utilisés :
  - Recherche :  
    `https://fr.openfoodfacts.org/cgi/search.pl?search_terms=...&json=1&page_size=20`
  - Détail produit :  
    `https://fr.openfoodfacts.org/api/v2/product/{code}.json`

[Site officiel OpenFoodFacts](https://fr.openfoodfacts.org/)

---

### 🥤 TheCocktailDB

- Base de données de cocktails
- Endpoint utilisé pour la recommandation :
  - `https://www.thecocktaildb.com/api/json/v1/1/filter.php?i={ingredient}`

Logiciel côté app :

- Nettoyage de l’ingrédient (accents, parenthèses, etc.)
- Mapping FR → EN (ex : _fraise → Strawberries_, _citron → Lemon_, _eau → Water_, etc.)
- Choix **aléatoire** d’un cocktail parmi la liste retournée pour **éviter de toujours avoir le même**.

[Site officiel TheCocktailDB](https://www.thecocktaildb.com/)

---

## 🚀 Installation & lancement

### 1. Prérequis

- **Android Studio** (Koala ou plus récent recommandé)
- **JDK 11**
- **Android SDK** avec API minimum 24 (Android 7.0)

### 2. Cloner le projet

```bash
git clone https://github.com/ton-compte/ton-projet.git
cd ton-projet
