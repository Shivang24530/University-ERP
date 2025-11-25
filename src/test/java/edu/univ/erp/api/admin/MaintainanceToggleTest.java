package test.java.edu.univ.erp.api.admin;


import test.java.edu.univ.erp.fakes.FakeERPDatabase;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MaintenanceToggleTest {

    @Test
    @DisplayName("Toggle maintenance ON")
    void maintenanceOn() {
        FakeERPDatabase db = new FakeERPDatabase();
        db.setMaintenance(true);
        assertTrue(db.isMaintenance());
    }

    @Test
    @DisplayName("Toggle maintenance OFF")
    void maintenanceOff() {
        FakeERPDatabase db = new FakeERPDatabase();
        db.setMaintenance(false);
        assertFalse(db.isMaintenance());
    }
}

