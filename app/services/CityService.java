package services;

import models.helpers.ActivityType;
import models.tables.City;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

/**
 * The type City service.
 */
@Singleton
public class CityService extends BaseService {

    private static final String ORDER_KEY = "name";
    private static final String LOG_CREATE = "has been added by the administrator.";
    private static final String LOG_DELETE = "has been deleted by the administrator.";
    private static final String LOG_EDIT = "has been edited by the administrator.";

    @Inject
    private CityService() {
    }

    /**
     * Gets all cities.
     *
     * @return the all cities
     */
    @SuppressWarnings("unchecked")
    public List<City> getAllCities() {
        return (List<City>) getSession().createCriteria(City.class)
                .addOrder(Order.asc(ORDER_KEY))
                .list();
    }

    /**
     * Gets city.
     *
     * @param locationId the location id
     * @return the city
     */
    public City getCity(final UUID locationId) {
        return (City) getSession().createCriteria(City.class)
                .add(Restrictions.eq("id", locationId))
                .uniqueResult();
    }

    /**
     * Create city boolean.
     *
     * @param city the city
     * @throws Exception the exception
     */
    public Boolean createCity(final City city) throws Exception {
        getSession().save(city);
        logActivity(ActivityType.ADMIN_CREATE,   city.getName() + LOG_CREATE);
        return true;
    }

    /**
     * Edit city boolean.
     *
     * @param city the city
     * @throws Exception the exception
     */
    public Boolean editCity(final City city) throws Exception {
        getSession().update(city);
        logActivity(ActivityType.ADMIN_EDIT, city.getName() + LOG_EDIT);
        return true;
    }

    /**
     * Delete city boolean.
     *
     * @param id the id
     * @throws Exception the exception
     */
    public Boolean deleteCity(final UUID id) throws Exception {
        City city = (City) getSession().createCriteria(City.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        getSession().delete(city);
        logActivity(ActivityType.ADMIN_DELETE, city.getName() + LOG_DELETE);
        return true;
    }
}
