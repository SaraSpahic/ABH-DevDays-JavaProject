package services;

import models.helpers.ActivityType;
import models.tables.Cuisine;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The type Cuisine service.
 */
@Singleton
public class CuisineService extends BaseService {

    private static final String ORDER_KEY = "name";
    private static final String LOG_CREATE = "has been added by the administrator.";
    private static final String LOG_DELETE = "has been deleted by the administrator.";
    private static final String LOG_EDIT = "has been edited by the administrator.";

    @Inject
    private CuisineService() {
    }

    /**
     * Gets all cuisines.
     *
     * @return the all cuisines
     */
    @SuppressWarnings("unchecked")
    public List<Cuisine> getAllCuisines() {
        return (List<Cuisine>) getSession().createCriteria(Cuisine.class)
                .addOrder(Order.asc(ORDER_KEY))
                .list();
    }

    /**
     * Gets all cuisines as string.
     *
     * @return the all cuisines as string
     */
    public List<String> getAllCuisinesAsString() {
        return this.getAllCuisines().stream()
                .map(Cuisine::getName)
                .collect(Collectors.toList());
    }

    /**
     * Gets cuisine.
     *
     * @param id the id
     * @return the cuisine
     */
    public Cuisine getCuisine(final UUID id) {
        return (Cuisine) getSession().createCriteria(Cuisine.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    /**
     * Create cuisine boolean.
     *
     * @param cuisine the cuisine
     * @return the boolean
     */
    public Boolean createCuisine(final Cuisine cuisine) {
        getSession().save(cuisine);
        logActivity(ActivityType.ADMIN_CREATE,  cuisine.getName() + LOG_CREATE);
        return true;
    }

    /**
     * Edit cuisine boolean.
     *
     * @param cuisine the cuisine
     * @return the boolean
     */
    public Boolean editCuisine(final Cuisine cuisine) {
        getSession().update(cuisine);
        logActivity(ActivityType.ADMIN_EDIT, cuisine.getName() + LOG_EDIT);
        return true;
    }

    /**
     * Delete cuisine boolean.
     *
     * @param id the id
     * @return the boolean
     */
    public Boolean deleteCuisine(final UUID id) {
        Cuisine cuisine = (Cuisine) getSession().createCriteria(Cuisine.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        getSession().delete(cuisine);
        logActivity(ActivityType.ADMIN_DELETE, cuisine.getName() + LOG_DELETE);
        return true;
    }
}
