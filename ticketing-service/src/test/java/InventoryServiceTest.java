import com.arquiweb.ticketing.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.inventory.domain.exceptions.IllegalTicketStateChangeException;
import com.arquiweb.ticketing.inventory.dto.internal.CapacityRefundDto;
import com.arquiweb.ticketing.inventory.dto.requests.InitInventoryRequest;
import com.arquiweb.ticketing.inventory.dto.requests.SectorCapacityDto;
import com.arquiweb.ticketing.inventory.dto.requests.SectorSeatsDto;
import com.arquiweb.ticketing.inventory.dto.responses.SectorAvailabilityResponse;
import com.arquiweb.ticketing.inventory.repository.SectorCapacityRepository;
import com.arquiweb.ticketing.inventory.repository.TicketRepository;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.*;

public class InventoryServiceTest {

    @Mock
    private SectorCapacityRepository scr;

    @Mock
    private TicketRepository ticketRepo;

    private InventoryService service;
    private AutoCloseable closeable;

    private static final String EVENT_ID = "64b1f2c3d4e5f6a7b8c9d0e1";
    private static final String SECTOR_ID = "sector-1";
    private static final UUID TICKET_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID RESERVATION_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeMethod
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        Mockito.reset(scr, ticketRepo);
        service = new InventoryService(scr, ticketRepo);
    }

    @AfterMethod
    void tearDown() throws Exception {
        if(closeable != null)
            closeable.close();
    }

    @Test
    public void testInitSector_SavesCapacitiesAndSeats() {

        // Arrange
        SectorSeatsDto seatsDto = mock(SectorSeatsDto.class);
        Ticket mockTicket = mock(Ticket.class);
        when(seatsDto.toTickets(EVENT_ID)).thenReturn(List.of(mockTicket));

        SectorCapacityDto capacityDto = mock(SectorCapacityDto.class);
        SectorCapacity mockCapacity = mock(SectorCapacity.class);
        when(capacityDto.toCapacity(EVENT_ID)).thenReturn(mockCapacity);

        InitInventoryRequest request = new InitInventoryRequest(
                EVENT_ID,
                List.of(capacityDto),
                List.of(seatsDto)
        );

        // Act
        service.initSector(request);

        // Assert
        verify(scr, times(1)).saveAll(anyList());
        verify(ticketRepo, times(1)).saveAll(anyList());
    }

    @Test
    public void testInitSector_EmptyCapacities_DoesNotSave() {

        // Arrange
        InitInventoryRequest request = new InitInventoryRequest(
                EVENT_ID,
                Collections.emptyList(),
                Collections.emptyList()
        );

        // Act
        service.initSector(request);

        // Assert
        verify(scr, never()).saveAll(anyList());
        verify(ticketRepo, never()).saveAll(anyList());
    }

    @Test
    public void testFindSectorAvailability_UnnumberedSectorFound_ReturnsResponse() {

        // Arrange
        SectorCapacity capacity = mock(SectorCapacity.class);
        when(scr.findByEventIdAndSectorId(EVENT_ID, SECTOR_ID)).thenReturn(Optional.of(capacity));

        // Act
        SectorAvailabilityResponse response = service.findSectorAvailability(EVENT_ID, SECTOR_ID);

        // Assert
        Assert.assertNotNull(response);
        verify(scr, times(1)).findByEventIdAndSectorId(EVENT_ID, SECTOR_ID);
        verify(ticketRepo, never()).findByEventIdAndSectorId(anyString(), anyString());
    }

    @Test
    public void testFindSectorAvailability_NumberedSectorFound_ReturnsResponse() {

        // Arrange
        when(scr.findByEventIdAndSectorId(EVENT_ID, SECTOR_ID)).thenReturn(Optional.empty());

        Ticket ticket = mock(Ticket.class);
        when(ticketRepo.findByEventIdAndSectorId(EVENT_ID, SECTOR_ID)).thenReturn(List.of(ticket));

        // Act
        SectorAvailabilityResponse response = service.findSectorAvailability(EVENT_ID, SECTOR_ID);

        // Assert
        Assert.assertNotNull(response);
        verify(scr, times(1)).findByEventIdAndSectorId(EVENT_ID, SECTOR_ID);
        verify(ticketRepo, times(1)).findByEventIdAndSectorId(EVENT_ID, SECTOR_ID);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testFindSectorAvailability_SectorNotFound_ThrowsResourceNotFoundException() {

        // Arrange
        when(scr.findByEventIdAndSectorId(EVENT_ID, SECTOR_ID)).thenReturn(Optional.empty());
        when(ticketRepo.findByEventIdAndSectorId(EVENT_ID, SECTOR_ID)).thenReturn(Collections.emptyList());

        // Act
        service.findSectorAvailability(EVENT_ID, SECTOR_ID);
    }

    @Test
    public void testGetSectorCapacity_Found_ReturnsCapacity() {

        // Arrange
        SectorCapacity capacity = mock(SectorCapacity.class);
        when(scr.findByEventIdAndSectorId(EVENT_ID, SECTOR_ID)).thenReturn(Optional.of(capacity));

        // Act
        SectorCapacity result = service.getSectorCapacity(EVENT_ID, SECTOR_ID);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result, capacity);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testGetSectorCapacity_NotFound() {

        // Arrange
        when(scr.findByEventIdAndSectorId(EVENT_ID, SECTOR_ID)).thenReturn(Optional.empty());

        // Act
        service.getSectorCapacity(EVENT_ID, SECTOR_ID);
    }

    @Test
    public void testSaveSectorCapacity_Successful() {

        // Arrange
        SectorCapacity capacity = mock(SectorCapacity.class);

        // Act
        service.saveSectorCapacity(capacity);

        // Assert
        verify(scr, times(1)).save(capacity);
    }

    @Test
    public void testFindTicketWithIdIn_DelegatesToRepository() {

        // Arrange
        List<UUID> ids = List.of(TICKET_ID);
        List<Ticket> tickets = List.of(mock(Ticket.class));
        when(ticketRepo.findByIdIn(ids)).thenReturn(tickets);

        // Act
        List<Ticket> result = service.findTicketWithIdIn(ids);

        // Assert
        Assert.assertEquals(result, tickets);
        verify(ticketRepo, times(1)).findByIdIn(ids);
    }

    @Test
    public void testReleaseNumberedTickets_DelegatesToRepository() {

        // Arrange
        List<UUID> reservations = List.of(RESERVATION_ID);

        // Act
        service.releaseNumberedTickets(reservations);

        // Assert
        verify(ticketRepo, times(1)).unlockNumberedTickets(reservations, TicketStatus.AVAILABLE);
    }

    @Test
    public void testDeleteUnnumberedTickets_DelegatesToRepository() {

        // Arrange
        List<UUID> reservations = List.of(RESERVATION_ID);

        // Act
        service.deleteUnnumberedTickets(reservations);

        // Assert
        verify(ticketRepo, times(1)).deleteExpiredUnnumbered(reservations);
    }

    @Test
    public void testCountCapacities_DelegatesToRepository() {

        // Arrange
        List<UUID> reservations = List.of(RESERVATION_ID);
        List<CapacityRefundDto> refunds = List.of(mock(CapacityRefundDto.class));
        when(ticketRepo.countUnnumberedCapacitiesToRefund(reservations)).thenReturn(refunds);

        // Act
        List<CapacityRefundDto> result = service.countCapacities(reservations);

        // Assert
        Assert.assertEquals(result, refunds);
        verify(ticketRepo, times(1)).countUnnumberedCapacitiesToRefund(reservations);
    }

    @Test
    public void testRefundCapacity_DelegatesToRepository() {

        // Arrange
        CapacityRefundDto refund = new CapacityRefundDto(EVENT_ID, SECTOR_ID, 5);

        // Act
        service.refundCapacity(refund);

        // Assert
        verify(scr, times(1)).refundCapacity(refund.eventId(), refund.sectorId(), refund.count());
    }

    @Test
    public void testConfirmTickets_Successful() {

        // Arrange
        Ticket ticket = mock(Ticket.class);
        when(ticket.getStatus()).thenReturn(TicketStatus.LOCKED_TEMPORARY);

        Set<Ticket> tickets = Set.of(ticket);

        // Act
        service.confirmTickets(tickets);

        // Assert
        verify(ticket, times(1)).setStatus(TicketStatus.SOLD);
        verify(ticket, times(1)).setQrToken(anyString());
    }

    @Test(expectedExceptions = IllegalTicketStateChangeException.class)
    public void testConfirmTickets_NotTemporarilyLocked() {

        // Arrange
        Ticket ticket = mock(Ticket.class);
        when(ticket.getStatus()).thenReturn(TicketStatus.AVAILABLE);

        Set<Ticket> tickets = Set.of(ticket);

        // Act
        service.confirmTickets(tickets);
    }

    @Test
    public void testUseTicket_Successful() {

        // Arrange
        Ticket ticket = mock(Ticket.class);
        when(ticket.getStatus()).thenReturn(TicketStatus.SOLD);
        when(ticketRepo.findById(TICKET_ID)).thenReturn(Optional.of(ticket));

        // Act
        service.useTicket(TICKET_ID);

        // Assert
        verify(ticket, times(1)).setStatus(TicketStatus.USED);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testUseTicket_NotFound() {

        // Arrange
        when(ticketRepo.findById(TICKET_ID)).thenReturn(Optional.empty());

        // Act
        service.useTicket(TICKET_ID);
    }

    @Test(expectedExceptions = IllegalTicketStateChangeException.class)
    public void testUseTicket_NotSold() {

        // Arrange
        Ticket ticket = mock(Ticket.class);
        when(ticket.getStatus()).thenReturn(TicketStatus.AVAILABLE);
        when(ticketRepo.findById(TICKET_ID)).thenReturn(Optional.of(ticket));

        // Act
        service.useTicket(TICKET_ID);
    }
}
