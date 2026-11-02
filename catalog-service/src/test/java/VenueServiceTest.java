
import com.arquiweb.catalog.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.catalog.venues.domain.Venue;
import com.arquiweb.catalog.venues.dto.request.CreateVenueRequest;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.repository.VenueRepository;
import com.arquiweb.catalog.venues.service.VenueService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService; // Clase REAL bajo prueba

    private AutoCloseable closeable;

    private static final String VALID_VENUE_ID = "64b1f2c3d4e5f6a7b8c9d0e2";

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test(priority = 1)
    public void testCreateVenue_Successful() {
        CreateVenueRequest request = mock(CreateVenueRequest.class);
        Venue mockVenue = mock(Venue.class);
        when(mockVenue.getId()).thenReturn(VALID_VENUE_ID);

        when(request.toDomain()).thenReturn(mockVenue);
        when(venueRepository.save(any())).thenReturn(mockVenue);

        String generatedId = venueService.createVenue(request);

        Assert.assertNotNull(generatedId);
        Assert.assertEquals(generatedId, VALID_VENUE_ID);
        verify(venueRepository, times(1)).save(any(Venue.class));
    }

    @Test
    public void testFindVenueById_Exists() {
        Venue mockVenue = mock(Venue.class);
        when(mockVenue.getId()).thenReturn(VALID_VENUE_ID);
        when(venueRepository.findById(VALID_VENUE_ID)).thenReturn(Optional.of(mockVenue));

        VenueResponse response = venueService.findVenueById(VALID_VENUE_ID);

        Assert.assertNotNull(response);
        verify(venueRepository, times(1)).findById(VALID_VENUE_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testFindVenueById_NoExists() {
        when(venueRepository.findById(VALID_VENUE_ID)).thenReturn(Optional.empty());

        venueService.findVenueById(VALID_VENUE_ID);
    }
}