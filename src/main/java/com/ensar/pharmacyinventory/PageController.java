package com.ensar.pharmacyinventory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;


@Controller
public class PageController {

    private final InventoryService inventoryService;
    private MedicationService medicationService;
    private TransactionService transactionService;
    private final UserService userService;
    private final SupplierService supplierService;

    PageController(
            MedicationService medicationService,
            TransactionService transactionService,
            InventoryService inventoryService,
            UserService userService, SupplierService supplierService) {

        this.medicationService = medicationService;
        this.transactionService = transactionService;
        this.inventoryService = inventoryService;
        this.userService = userService;
        this.supplierService = supplierService;
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

        User user = userService.findByEmail(email);

        if (user == null) {
            return "redirect:/login?error=true";
        }

        if (!userService.passwordMatches(password, user.getPasswordHash())) {
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
                medicationService.getMedicationCount()
        );

        model.addAttribute(
                "lowStockCount",
                            inventoryService.getLowStock().size()
        );

        model.addAttribute(
                "expiringSoonCount",
                inventoryService.getExpiringSoon(LocalDate.now().plusDays(90)).size()
        );

        model.addAttribute(
                "totalTransactions",
                transactionService.getTransactionCount()
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

        model.addAttribute("inventory", inventoryService.getAllInventory());

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

        model.addAttribute("transactions", transactionService.getAllTransactions());
        model.addAttribute("inventory", inventoryService.getAllInventory());

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
                inventoryService.getLowStock()
        );

        model.addAttribute(
                "expiringSoon",
                inventoryService.getExpiringSoon(LocalDate.now().plusDays(90)
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
                inventoryService.getLowStock()
        );

        model.addAttribute(
                "mostUsedMedications",
                transactionService.getMostUsedMedications()
        );

        model.addAttribute(
                "monthlyUsageTrends",
                transactionService.getMonthlyUsageTrends()
        );

        model.addAttribute(
                "highRiskMedications",
                transactionService.getHighRiskMedications()
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
                supplierService.getAllSuppliers()
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
                supplierService.getAllSuppliers()
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
                            userService.getAllUsers()
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
        if (userService.emailExists(email)) {
            return "redirect:/manageUsers?emailError=true";
        }

        userService.createUser(
                first_name,
                last_name,
                email,
                password,
                role
        );

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

        userService.deleteUser(userID);
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
                medicationService.searchMedications(searchTerm)
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
                inventoryService.getAllOrderByExpirationDate()
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