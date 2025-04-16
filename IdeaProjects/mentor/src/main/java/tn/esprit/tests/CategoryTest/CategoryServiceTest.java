package tn.esprit.tests.CategoryTest;

import tn.esprit.entities.Category;
import tn.esprit.services.CategoryService;
import java.time.LocalDateTime;
import java.util.Optional;

public class CategoryServiceTest {
    private static final CategoryService categoryService = CategoryService.getInstance();

    public static void main(String[] args) {
        try {
            System.out.println("=== Début des tests CategoryService ===");

            // Test 1: Création de catégorie
            Category testCategory = createCategoryTest();

            // Test 2: Modification de catégorie
            modifyCategoryTest(testCategory);

            // Test 3: Suppression de catégorie
          //  deleteCategoryTest(testCategory);

            // Test 4: Vérification finale
            listAllCategoriesTest();

            System.out.println("=== Tous les tests ont été exécutés avec succès ===");
        } catch (Exception e) {
            System.err.println("❌ Erreur critique pendant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Category createCategoryTest() {
        System.out.println("\n--- Test 1: Création de catégorie ---");
        Category category = new Category(
                "Test Category " + System.currentTimeMillis(),
                "Description de test",
                LocalDateTime.now(),
                true,
                "fa-test-icon"
        );

        categoryService.ajouter(category);
        System.out.println("✅ Catégorie créée: " + category.getName() + " (ID: " + category.getId() + ")");

        // Vérification
        Category retrieved = categoryService.getOne(category);
        if (retrieved == null || !retrieved.getName().equals(category.getName())) {
            throw new AssertionError("La catégorie n'a pas été correctement persistée");
        }

        return category;
    }

    private static void modifyCategoryTest(Category category) {
        System.out.println("\n--- Test 2: Modification de catégorie ---");
        String newName = "Updated " + category.getName();
        String newIcon = "fa-updated-icon";

        category.setName(newName);
        category.setIcon(newIcon);
        category.setIsActive(false);

        categoryService.modifier(category);
        System.out.println("✅ Catégorie modifiée: " + category.getName());

        // Vérification
        Category updated = categoryService.getOne(category);
        if (updated == null || !updated.getName().equals(newName)) {
            throw new AssertionError("La modification n'a pas été persistée");
        }
        System.out.println("Vérification OK: Nom='" + updated.getName() +
                "', Icon='" + updated.getIcon() +
                "', Active=" + updated.getIsActive());
    }

   /* private static void deleteCategoryTest(Category category) {
        System.out.println("\n--- Test 3: Suppression de catégorie ---");
        int idToDelete = category.getId();

        categoryService.supprimer(idToDelete);
        System.out.println("✅ Catégorie supprimée (ID: " + idToDelete + ")");

        // Vérification
        Category deleted = categoryService.getOne(category);
        if (deleted != null) {
            throw new AssertionError("La catégorie existe toujours après suppression");
        }
        System.out.println("Vérification OK: La catégorie a bien été supprimée");
    }*/

    private static void listAllCategoriesTest() {
        System.out.println("\n--- Test 4: Liste des catégories ---");
        var categories = categoryService.getAll();

        if (categories.isEmpty()) {
            System.out.println("ℹ️ Aucune catégorie trouvée");
            return;
        }

        System.out.println("📋 " + categories.size() + " catégorie(s) trouvée(s):");
        categories.forEach(cat -> {
            System.out.println("\nID: " + cat.getId());
            System.out.println("Nom: " + cat.getName());
            System.out.println("Description: " + cat.getDescription());
            System.out.println("Active: " + cat.getIsActive());
            System.out.println("Icon: " + cat.getIcon());
            System.out.println("Créé le: " + cat.getCreatedAt());
        });
    }

    // Méthode utilitaire pour trouver une catégorie par ID
    private static Optional<Category> findCategoryById(int id) {
        return categoryService.getAll()
                .stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }
}