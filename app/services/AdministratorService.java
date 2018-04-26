package services;

import models.helpers.AdministratorStatistics;
import models.tables.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * The type Administrator service.
 */
@Singleton
public class AdministratorService extends BaseService {

    private String ACTIVITY_ORDER_KEY = "timestamp";

    @Inject
    private AdministratorService() {
    }

    public AdministratorStatistics getStatistics() {
        return new AdministratorStatistics(
                ((Number) getSession().createCriteria(Restaurant.class)
                        .setProjection(Projections.rowCount()).uniqueResult()).intValue(),
                ((Number) getSession().createCriteria(Cuisine.class)
                        .setProjection(Projections.rowCount()).uniqueResult()).intValue(),
                ((Number) getSession().createCriteria(City.class)
                        .setProjection(Projections.rowCount()).uniqueResult()).intValue(),
                ((Number) getSession().createCriteria(User.class)
                        .setProjection(Projections.rowCount()).uniqueResult()).intValue()
        );
    }

    public List<ActivityLog> getActivityLogs() {
        return (List<ActivityLog>) getSession().createCriteria(ActivityLog.class)
                .addOrder(Order.desc(ACTIVITY_ORDER_KEY))
                .list();

    }
}
