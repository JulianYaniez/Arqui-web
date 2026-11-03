import com.arquiweb.ticketing.inventory.repository.SectorCapacityRepository;
import com.arquiweb.ticketing.inventory.repository.TicketRepository;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class InvetoryServiceTest {

    @Mock
    private SectorCapacityRepository scr;

    @Mock
    private TicketRepository ticketRepo;

    private InventoryService service;
    private AutoCloseable closeable;

    @BeforeMethod
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(scr, service);
        service = new InventoryService(scr, ticketRepo);
    }

    @AfterMethod
    void tearDown() throws Exception {
        if(closeable != null)
            closeable.close();
    }


}
