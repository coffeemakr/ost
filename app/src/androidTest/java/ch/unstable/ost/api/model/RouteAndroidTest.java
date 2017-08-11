package ch.unstable.ost.api.model;

import org.junit.Test;

import java.util.Date;

import ch.unstable.ost.TestHelper;

import static org.junit.Assert.*;


public class RouteAndroidTest {

    private static PassingCheckpoint[] generatePassingCheckpoints(int number) {
        PassingCheckpoint[] checkpoints = new PassingCheckpoint[number];
        for(int i = 0; i < number; ++i) {
            Date arrival = new Date(number);
            Date departure = new Date(number);
            Location location = new Location("" + number, Location.StationType.TRAIN, "" + number);
            checkpoints[i] = new PassingCheckpoint(arrival, departure, location, "" + number);
        }
        return checkpoints;
    }

    @Test
    public void testParcelable() {
        Route route;
        String shortname = "short name";
        String longname = "long name";
        PassingCheckpoint[] stops = generatePassingCheckpoints(20);
        route = new Route(shortname, longname, stops);

        Route readRoute = TestHelper.writeAndRead(route, Route.CREATOR);
        assertEquals(longname, readRoute.getLongName());
        assertEquals(shortname, readRoute.getShortName());
        assertArrayEquals(stops, readRoute.getStops());
        assertEquals(readRoute, route);

    }
}