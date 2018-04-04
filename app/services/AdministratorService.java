package services;

import models.helpers.AdministratorStatistics;
import models.tables.City;
import models.tables.Cuisine;
import models.tables.Restaurant;
import models.tables.User;
import org.hibernate.criterion.Projections;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The type Administrator service.
 */
@Singleton
public class AdministratorService extends BaseService {

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

}
