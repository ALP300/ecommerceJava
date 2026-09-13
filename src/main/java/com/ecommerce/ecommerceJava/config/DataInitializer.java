package com.ecommerce.ecommerceJava.config;

import com.ecommerce.ecommerceJava.model.Category;
import com.ecommerce.ecommerceJava.model.Product;
import com.ecommerce.ecommerceJava.model.Role;
import com.ecommerce.ecommerceJava.model.User;
import com.ecommerce.ecommerceJava.repository.CategoryRepository;
import com.ecommerce.ecommerceJava.repository.ProductRepository;
import com.ecommerce.ecommerceJava.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ProductRepository productRepository,
                           CategoryRepository categoryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initCategories();
        initUsers();
        initProducts();
    }

    private void initCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> baseCategories = List.of(
                    Category.builder().nombre("iPhone").slug("iphone").descripcion("Potencia y diseño en tu bolsillo").orden(1).activa(true).build(),
                    Category.builder().nombre("Laptops").slug("laptops").descripcion("Rendimiento para crear sin límites").orden(2).activa(true).build(),
                    Category.builder().nombre("Cargadores").slug("cargadores").descripcion("Energía rápida y sin enredos").orden(3).activa(true).build(),
                    Category.builder().nombre("Audífonos").slug("audifonos").descripcion("Sonido envolvente todo el día").orden(4).activa(true).build()
            );
            categoryRepository.saveAll(baseCategories);
            logger.info(">>> Categorías base inicializadas.");
        }
    }

    private void initUsers() {
        // Inicializar Administrador por defecto
        if (!userRepository.existsByUsername("admin") && !userRepository.existsByEmail("admin@ecommerce.com")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@ecommerce.com")
                    .fullName("Administrador Principal")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
            logger.info(">>> Usuario Administrador creado: admin@ecommerce.com / admin123 (ROLE_ADMIN)");
        }

        // Inicializar Usuario por defecto
        if (!userRepository.existsByUsername("cliente") && !userRepository.existsByEmail("user@ecommerce.com")) {
            User user = User.builder()
                    .username("cliente")
                    .email("user@ecommerce.com")
                    .fullName("Cliente de Prueba")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.ROLE_USER)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);
            logger.info(">>> Usuario Cliente creado: user@ecommerce.com / user123 (ROLE_USER)");
        }
    }

    private void initProducts() {
        if (productRepository.count() == 0) {
            Category iphoneCat = categoryRepository.findBySlug("iphone").orElse(null);
            Category laptopCat = categoryRepository.findBySlug("laptops").orElse(null);
            Category audioCat = categoryRepository.findBySlug("audifonos").orElse(null);

            List<Product> initialProducts = List.of(
                    Product.builder()
                            .name("iPhone 15 Pro Max 256GB")
                            .description("Diseño de titanio aeroespacial con chip A17 Pro, botón de acción y cámaras Pro.")
                            .price(1199.99)
                            .stock(15)
                            .category(iphoneCat)
                            .imageUrl("https://images.unsplash.com/photo-1695048133142-1a20484d2569")
                            .build(),
                    Product.builder()
                            .name("iPhone 15 128GB")
                            .description("Dynamic Island, cámara principal de 48 MP y chip A16 Bionic en un diseño resistente.")
                            .price(799.99)
                            .stock(25)
                            .category(iphoneCat)
                            .imageUrl("https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5")
                            .build(),
                    Product.builder()
                            .name("MacBook Air 13\" M3")
                            .description("Increíblemente delgada y rápida con chip M3, hasta 18 horas de batería y pantalla Liquid Retina.")
                            .price(1099.00)
                            .stock(10)
                            .category(laptopCat)
                            .imageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8")
                            .build(),
                    Product.builder()
                            .name("AirPods Pro (2da Generación) USB-C")
                            .description("Cancelación activa de ruido 2 veces mejor, audio adaptativo y estuche MagSafe USB-C.")
                            .price(249.00)
                            .stock(40)
                            .category(audioCat)
                            .imageUrl("https://images.unsplash.com/photo-1600294037681-c80b4cb5b434")
                            .build()
            );

            productRepository.saveAll(initialProducts);
            logger.info(">>> Catálogo inicial de productos registrado en la base de datos.");
        }
    }
}
