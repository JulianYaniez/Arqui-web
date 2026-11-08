package com.arquiweb.catalog.dev.service;

import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.EventSector;
import com.arquiweb.catalog.events.domain.enums.EventType;
import com.arquiweb.catalog.events.repository.EventRepository;
import com.arquiweb.catalog.events.service.EventService;
import com.arquiweb.catalog.venues.domain.Location;
import com.arquiweb.catalog.venues.domain.Sector;
import com.arquiweb.catalog.venues.domain.Venue;
import com.arquiweb.catalog.venues.dto.response.SectorResponse;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DevService {

    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public void seed() {
        Random random = new Random();
        Faker faker = new Faker();

        List<Venue> venuesCreation = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            List<Sector> sectors = new ArrayList<>();
            int sectorsAmount = random.nextInt(4, 11);

            for (int j = 0; j < sectorsAmount; j++) {
                String name = faker.expression("#{options.option 'Block','Section','Zone'} #{numerify '#0#'}");
                boolean isNumbered = random.nextBoolean();
                int capacity = 0;
                int rows = 0;
                int seatsPerRow = 0;
                if (isNumbered) {
                    rows = random.nextInt(1, 13);
                    seatsPerRow = random.nextInt(1, 25);
                } else {
                    capacity = random.nextInt(1, 31) * 100;
                }
                sectors.add(new Sector(
                        name,
                        isNumbered,
                        capacity,
                        rows,
                        seatsPerRow
                ));
            }

            String venueType = faker.options().option("Stadium", "Arena", "Center", "Coliseum", "Field", "Park");
            String name = faker.company().name() + " " + venueType;
            String city = faker.address().city();
            String address = faker.address().fullAddress();
            Location location = new Location(random.nextLong(-90, 90), random.nextLong(-180, 180));

            venuesCreation.add(new Venue(
                    name,
                    address,
                    city,
                    location,
                    sectors
            ));
        }

        List<Venue> venues = venueRepository.saveAll(venuesCreation);

        List<Event> eventsCreation = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            Venue eventVenue = venues.get(random.nextInt(0, venues.size()));
            List<EventSector> eventSectors = new ArrayList<>();

            int eventSectorsAmount = random.nextInt(1, eventVenue.getSectors().size());

            for (int j = 0; j < eventSectorsAmount; j++) {
                BigDecimal price = BigDecimal.valueOf(random.nextDouble(100, 10000));
                EventSector eventSector = new EventSector(
                        SectorResponse.fromDomain(eventVenue.getSectors().get(j)),
                        price
                );
                eventSectors.add(eventSector);
            }

            EventType eventType = faker.options().option(EventType.values());
            String eventTypeString = eventType.name().substring(0, 1).toUpperCase() + eventType.name().substring(1).toLowerCase();
            String name = faker.minecraft().itemName() + " " + eventTypeString;
            String description = faker.lorem().characters(40, 100);
            LocalDateTime dateTime = LocalDateTime.ofInstant(faker.timeAndDate().between(
                        Instant.now().plus(Duration.ofDays(100)),
                        Instant.now().plus(Duration.ofDays(600))),
                    ZoneId.systemDefault());
            String organizerId = faker.internet().uuidv7();
            VenueResponse venueResponse = VenueResponse.fromDomain(eventVenue);

            Set<String> participants = new HashSet<>();
            for (int j = 0; j < 12; j++) {
                participants.add(faker.artist().name());
            }


            eventsCreation.add(
                    new Event(
                            name,
                            description,
                            eventType,
                            dateTime,
                            organizerId,
                            participants.stream().toList(),
                            venueResponse,
                            eventSectors
                    )
            );
        }

        List<Event> events = eventRepository.saveAll(eventsCreation);

        for (int i = 0; i < random.nextInt(10); i++) {
            eventService.publishEvent(events.get(i).getId());
        }
    }
}
