package tn.esprit.tests.CoursesTest;

import tn.esprit.entities.Category;
import tn.esprit.entities.Courses;
import tn.esprit.services.CategoryService;
import tn.esprit.services.CoursesService;

import java.time.Instant;
import java.util.Optional;

public class CoursesServiceTest {
    private static final CoursesService coursesService = CoursesService.getInstance();
    private static final CategoryService categoryService = CategoryService.getInstance();

    public static void main(String[] args) {
        try {
            System.out.println("=== Début des tests CoursesService ===");

            // Prérequis: Créer une catégorie de test
            Category testCategory = createTestCategory();

            // Test 1: Création de cours
            Courses testCourse = createCourseTest(testCategory);

            // Test 2: Modification de cours
            modifyCourseTest(testCourse);

            // Test 3: Suppression de cours
            deleteCourseTest(testCourse);

            // Test 4: Vérification finale
            listAllCoursesTest();

            System.out.println("=== Tous les tests ont été exécutés avec succès ===");
        } catch (Exception e) {
            System.err.println("❌ Erreur critique pendant les tests: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Nettoyage des données de test
            cleanUpTestData();
        }
    }

    private static Category createTestCategory() {
        Category category = new Category(
                "Test Category " + System.currentTimeMillis(),
                "Description de test",
                java.time.LocalDateTime.now(),
                true,
                "fa-test-icon"
        );
        categoryService.ajouter(category);
        System.out.println("✅ Catégorie de test créée: " + category.getName() + " (ID: " + category.getId() + ")");
        return category;
    }

    private static Courses createCourseTest(Category category) {
        System.out.println("\n--- Test 1: Création de cours ---");
        Courses course = new Courses();
        course.setTitle("Cours de Test " + System.currentTimeMillis());
        course.setDescription("Description du cours de test");
        course.setIsPublished(true);
        course.setProgressPointsRequired(100);
        course.setCreatedAt(Instant.now());
        course.setCategory(category);
        course.setIsPremium(false);
        course.setTutorName("Tuteur Test");

        coursesService.ajouter(course);
        System.out.println("✅ Cours créé: " + course.getTitle() + " (ID: " + course.getId() + ")");

        // Vérification
        Courses retrieved = coursesService.getOne(course);
        if (retrieved == null || retrieved.getId() != course.getId()) {
            throw new AssertionError("Échec: Le cours n'a pas été correctement persisté");
        }
        System.out.println("Vérification OK: Cours trouvé en base de données");

        return course;
    }

    private static void modifyCourseTest(Courses course) {
        System.out.println("\n--- Test 2: Modification de cours ---");
        String newTitle = "Modifié " + course.getTitle();
        String newDescription = "Nouvelle description mise à jour";
        boolean newStatus = false;

        course.setTitle(newTitle);
        course.setDescription(newDescription);
        course.setIsPublished(newStatus);

        coursesService.modifier(course);
        System.out.println("✅ Cours modifié: " + course.getTitle());

        // Vérification
        Courses updated = coursesService.getOne(course);
        if (updated == null || updated.getId() != course.getId()) {
            throw new AssertionError("Échec: Le cours modifié n'a pas été trouvé");
        }
        if (!updated.getTitle().equals(newTitle)) {
            throw new AssertionError("Échec: Le titre n'a pas été mis à jour");
        }
        if (!updated.getDescription().equals(newDescription)) {
            throw new AssertionError("Échec: La description n'a pas été mise à jour");
        }
        if (updated.getIsPublished() != newStatus) {
            throw new AssertionError("Échec: Le statut de publication n'a pas été mis à jour");
        }
        System.out.println("Vérification OK: Toutes les modifications ont été persistées");
    }

    private static void deleteCourseTest(Courses course) {
        System.out.println("\n--- Test 3: Suppression de cours ---");
        int idToDelete = course.getId();

        // Vérification pré-suppression
        if (coursesService.getOne(course) == null) {
            throw new AssertionError("Échec: Le cours à supprimer n'existe pas");
        }

        coursesService.supprimer(idToDelete);
        System.out.println("✅ Cours supprimé (ID: " + idToDelete + ")");

        // Vérification post-suppression
        Courses deleted = coursesService.getOne(course);
        if (deleted != null) {
            throw new AssertionError("Échec: Le cours existe toujours après suppression");
        }
        System.out.println("Vérification OK: Le cours a bien été supprimé de la base de données");
    }

    private static void listAllCoursesTest() {
        System.out.println("\n--- Test 4: Liste des cours ---");
        var courses = coursesService.getAll();

        if (courses.isEmpty()) {
            System.out.println("ℹ️ Aucun cours trouvé (ceci est normal après suppression)");
            return;
        }

        System.out.println("📋 " + courses.size() + " cours trouvés:");
        courses.forEach(c -> {
            System.out.println("\nID: " + c.getId());
            System.out.println("Titre: " + c.getTitle());
            System.out.println("Catégorie: " + c.getCategory().getName());
            System.out.println("Statut: " + (c.getIsPublished() ? "Publié" : "Non publié"));
        });
    }

    private static void cleanUpTestData() {
        System.out.println("\n--- Nettoyage des données de test ---");
        // Suppression des cours de test restants
        coursesService.getAll().stream()
                .filter(c -> c.getTitle().contains("Test") || c.getTitle().contains("Modifié"))
                .forEach(c -> {
                    coursesService.supprimer(c.getId());
                    System.out.println("Nettoyage: Cours supprimé - " + c.getTitle());
                });

        // Suppression des catégories de test
        categoryService.getAll().stream()
                .filter(cat -> cat.getName().contains("Test"))
                .forEach(cat -> {
                    categoryService.supprimer(cat.getId());
                    System.out.println("Nettoyage: Catégorie supprimée - " + cat.getName());
                });
    }

    // Méthode utilitaire pour trouver un cours par ID
    private static Optional<Courses> findCourseById(int id) {
        Courses temp = new Courses();
        temp.setId(id);
        return Optional.ofNullable(coursesService.getOne(temp));
    }
}