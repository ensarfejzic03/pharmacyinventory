package com.ensar.pharmacyinventory;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class PageController {

    private MedicationRepository medicationRepository;
    private SupplierRepository supplierRepository;
    private MedicationService medicationService;
    private InventoryRepository inventoryRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;
    private UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    PageController(MedicationRepository medicationRepository,
                   SupplierRepository supplierRepository,
                   MedicationService medicationService,
                   InventoryRepository inventoryRepository,
                   TransactionRepository transactionRepository,
                   TransactionService transactionService,
                   UserRepository userRepository) {

        this.medicationRepository = medicationRepository;
        this.supplierRepository = supplierRepository;
        this.medicationService = medicationService;
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login?error=true";
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return "redirect:/login?error=true";
        }

        session.setAttribute("userID", user.getUserID());
        session.setAttribute("firstName", user.getFirstName());
        session.setAttribute("lastName", user.getLastName());
        session.setAttribute("role", user.getRole());

        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(HttpSession session, Model model){

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        addUserToModel(session, model);

        model.addAttribute(
                "totalMedications",
                medicationRepository.count()
        );

        model.addAttribute(
                "lowStockCount",
                inventoryRepository.findLowStock().size()
        );

        model.addAttribute(
                "expiringSoonCount",
                inventoryRepository.findExpiringSoon(
                        LocalDate.now().plusDays(90)
                ).size()
        );

        model.addAttribute(
                "totalTransactions",
                transactionRepository.count()
        );

        return "dashboard";
    }

    @GetMapping("/medications")
    public String medicationPage(Model model, HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        addUserToModel(session, model);

        model.addAttribute("medications", medicationService.getAllMedications());
        return "medications";
    }

    @GetMapping("/inventory")
    public String inventoryPage(Model model, HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        addUserToModel(session, model);

        model.addAttribute("inventory", inventoryRepository.findAll());

        return "inventory";
    }

    @GetMapping("/transactions")
    public String transactionsPage(
            @RequestParam(required = false) Integer inventoryID,
            Model model,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        addUserToModel(session, model);

        model.addAttribute("transactions", transactionRepository.findAll());
        model.addAttribute("inventory", inventoryRepository.findAll());

        model.addAttribute("selectedInventoryID", inventoryID);

        model.addAttribute(
                "canUpdateStock",
                !hasRole(session, "Technician")
        );

        return "transactions";
    }

    @PostMapping("/transactions")
    public String recordTransaction(
            @RequestParam int inventory_id,
            @RequestParam String transaction_type,
            @RequestParam int amount_changed,
            HttpSession session) {

        Integer userID = (Integer) session.getAttribute("userID");

        if (userID == null) {
            return "redirect:/login";
        }

        if (hasRole(session, "Technician")) {
            return "redirect:/transactions";
        }

        transactionService.recordTransaction(
                inventory_id,
                userID,
                transaction_type,
                amount_changed
        );

        return "redirect:/transactions";
    }

    @GetMapping("/alerts")
    public String alertsPage(HttpSession session, Model model) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        addUserToModel(session, model);

        model.addAttribute(
                "lowStock",
                inventoryRepository.findLowStock()
        );

        model.addAttribute(
                "expiringSoon",
                inventoryRepository.findExpiringSoon(
                        LocalDate.now().plusDays(90)
                )
        );

        return "alerts";
    }

    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model){

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (hasRole(session, "Technician")) {
            return "redirect:/dashboard";
        }

        addUserToModel(session, model);

        model.addAttribute(
                "lowStock",
                inventoryRepository.findLowStock()
        );

        model.addAttribute(
                "mostUsedMedications",
                transactionRepository.findMostUsedMedications()
        );

        model.addAttribute(
                "monthlyUsageTrends",
                transactionRepository.findMonthlyUsageTrends()
        );

        model.addAttribute(
                "highRiskMedications",
                transactionRepository.findHighRiskMedications()
        );

        return "reports";
    }

    @GetMapping("/addMedication")
    public String addMedicationPage(Model model, HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (!hasRole(session, "Admin")
                && !hasRole(session, "Pharmacist")) {

            return "redirect:/dashboard";
        }

        addUserToModel(session, model);

        model.addAttribute(
                "suppliers",
                supplierRepository.findAll()
        );

        return "addMedication";
    }

    @PostMapping("/addMedication")
    public String createMedication(
            @RequestParam String medication_name,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam int reorder_threshold,
            @RequestParam int supplier_id,
            @RequestParam int quantity,
            @RequestParam String expiration_date,
            @RequestParam String location,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (!hasRole(session, "Admin")
                && !hasRole(session, "Pharmacist")) {

            return "redirect:/dashboard";
        }

        medicationService.createMedicationWithInventory(
                medication_name,
                category,
                description,
                reorder_threshold,
                supplier_id,
                quantity,
                expiration_date,
                location
        );

        return "redirect:/medications";
    }

    @GetMapping("/editMedication")
    public String editMedicationPage(
            @RequestParam int id,
            Model model,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (!hasRole(session, "Admin")
                && !hasRole(session, "Pharmacist")) {

            return "redirect:/dashboard";
        }

        addUserToModel(session, model);

        Medication medication = medicationService.getMedicationById(id);

        model.addAttribute("medication", medication);

        model.addAttribute(
                "suppliers",
                supplierRepository.findAll()
        );

        return "editMedication";
    }

    @PostMapping("/editMedication")
    public String updateMedication(
            @RequestParam int medicationID,
            @RequestParam String medication_name,
            @RequestParam String category,
            @RequestParam int reorder_threshold,
            @RequestParam int supplier_id,
            @RequestParam String description,
            HttpSession session) {

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (!hasRole(session, "Admin")
                && !hasRole(session, "Pharmacist")) {

            return "redirect:/dashboard";
        }

        medicationService.updateMedication(
                medicationID,
                medication_name,
                category,
                reorder_threshold,
                supplier_id,
                description
        );

        return "redirect:/medications";
    }



    @GetMapping("/manageUsers")
    public String manageUsersPage(HttpSession session, Model model){

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (!hasRole(session, "Admin")) {
            return "redirect:/dashboard";
        }

        addUserToModel(session, model);

        model.addAttribute(
                "users",
                userRepository.findAll()
        );

        return "manageUsers";
    }

    @PostMapping("/manageUsers")
    public String createUser(
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            HttpSession session){

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (!hasRole(session, "Admin")) {
            return "redirect:/dashboard";
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/manageUsers?emailError=true";
        }
        User user = new User();

        user.setFirstName(first_name);
        user.setLastName(last_name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "redirect:/manageUsers";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(
            @RequestParam int userID,
            HttpSession session){

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (!hasRole(session, "Admin")) {
            return "redirect:/dashboard";
        }

        Integer loggedInUserID = (Integer) session.getAttribute("userID");

        if (loggedInUserID != null && loggedInUserID == userID) {
            return "redirect:/manageUsers";
        }

        userRepository.deleteById(userID);

        return "redirect:/manageUsers";
    }

    @GetMapping("/searchMedication")
    public String searchMedicationPage(
            @RequestParam String searchTerm,
            HttpSession session,
            Model model){

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        addUserToModel(session, model);

        model.addAttribute(
                "medications",
                medicationRepository.findByNameContainingIgnoreCase(searchTerm)
        );

        model.addAttribute("searchTerm", searchTerm);

        return "searchMedication";
    }

    @GetMapping("/sortMedication")
    public String sortMedicationPage(HttpSession session, Model model){

        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        addUserToModel(session, model);

        model.addAttribute(
                "inventory",
                inventoryRepository.findAllOrderByExpirationDate()
        );

        return "sortMedication";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("userID") != null;
    }

    private boolean hasRole(
            HttpSession session,
            String role) {

        return role.equals(
                session.getAttribute("role")
        );
    }

    private void addUserToModel(
            HttpSession session,
            Model model) {

        model.addAttribute(
                "firstName",
                session.getAttribute("firstName")
        );

        model.addAttribute(
                "lastName",
                session.getAttribute("lastName")
        );

        model.addAttribute(
                "role",
                session.getAttribute("role")
        );
    }
}