package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.model.MetricPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static com.txstate.bloodhound.testutil.TestDataFactory.measurement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChartDataServiceTest {

    @Mock
    private HealthMeasurementDao dao;

    private ChartDataService service;

    @BeforeEach
    void setUp() {
        service = new ChartDataService(dao);
    }

    @Test
    void getSystolicTrend_buildsTrendPointsInChronologicalOrder() throws SQLException {
        LocalDateTime t1 = LocalDateTime.of(2026, 1, 2, 7, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 1, 7, 0);
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 3, 7, 0);

        HealthMeasurement m1 = measurement(1L, 1L, 132, 84, 200, 50, 130, 180.0, t1);
        HealthMeasurement m2 = measurement(2L, 1L, 130, 82, 198, 49, 128, 179.0, t2);
        HealthMeasurement m3 = measurement(3L, 1L, 134, 86, 202, 51, 132, 181.0, t3);

        when(dao.findByUserIdOrderedByDate(1L)).thenReturn(List.of(m1, m2, m3));

        List<MetricPoint> points = service.getSystolicTrend(1L);

        assertEquals(3, points.size());
        assertEquals(t2, points.get(0).getTimestamp());
        assertEquals(t1, points.get(1).getTimestamp());
        assertEquals(t3, points.get(2).getTimestamp());
    }

    @Test
    void getWeightTrend_skipsNullValues() throws SQLException {
        LocalDateTime t1 = LocalDateTime.of(2026, 1, 1, 7, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 2, 7, 0);

        HealthMeasurement withWeight = measurement(1L, 1L, 132, 84, 200, 50, 130, 180.0, t1);
        HealthMeasurement withoutWeight = measurement(2L, 1L, 130, 82, 198, 49, 128, null, t2);

        when(dao.findByUserIdOrderedByDate(1L)).thenReturn(List.of(withWeight, withoutWeight));

        List<MetricPoint> points = service.getWeightTrend(1L);

        assertEquals(1, points.size());
        assertEquals(t1, points.get(0).getTimestamp());
        assertEquals(180.0, points.get(0).getValue());
    }

    @Test
    void getLdlTrend_returnsUserScopedDataOnly() throws SQLException {
        LocalDateTime t1 = LocalDateTime.of(2026, 1, 1, 7, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 2, 7, 0);

        HealthMeasurement userOne = measurement(1L, 1L, 132, 84, 200, 50, 130, 180.0, t1);
        HealthMeasurement userTwo = measurement(2L, 2L, 140, 90, 220, 45, 150, 210.0, t2);

        when(dao.findByUserIdOrderedByDate(1L)).thenReturn(List.of(userOne));
        when(dao.findByUserIdOrderedByDate(2L)).thenReturn(List.of(userTwo));

        List<MetricPoint> userOnePoints = service.getLdlTrend(1L);
        List<MetricPoint> userTwoPoints = service.getLdlTrend(2L);

        assertEquals(1, userOnePoints.size());
        assertEquals(130.0, userOnePoints.get(0).getValue());
        assertEquals(1, userTwoPoints.size());
        assertEquals(150.0, userTwoPoints.get(0).getValue());
    }
}
