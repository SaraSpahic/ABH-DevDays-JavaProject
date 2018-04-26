package services;

import controllers.RestaurantController;
import models.helpers.*;
import models.helpers.forms.ImageUploadForm;
import models.helpers.forms.ReviewForm;
import models.tables.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;
import scala.Console;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The type Restaurant service.
 */
@Singleton
public class RestaurantService extends BaseService {

    private static final String AWS_BASE_PATH = "/assets/images/";
    private static final String LOG_CREATE = "has been added by the administrator.";
    private static final String LOG_DELETE = "has been deleted by the administrator.";
    private static final String LOG_EDIT = "has been edited by the administrator.";
    private static final String LOG_REVIEW = "A review has been posted for restaurant ";
    private static final String LOG_IMAGE = "Image has been updated for restaurant ";

    @Inject
    private RestaurantService() {
    }

    /**
     * Create restaurant boolean.
     *
     * @param restaurant the restaurant
     * @throws Exception the exception
     */
    public Boolean createRestaurant(final Restaurant restaurant) throws Exception {
        getSession().save(restaurant);
        logActivity(ActivityType.ADMIN_CREATE,  restaurant.getName() + LOG_CREATE);
        return true;
    }

    /**
     * Edit restaurant boolean.
     *
     * @param restaurant the restaurant
     * @throws Exception the exception
     */
    public Boolean editRestaurant(final Restaurant restaurant) throws Exception {
        getSession().merge(restaurant);
        logActivity(ActivityType.ADMIN_EDIT, restaurant.getName() + LOG_EDIT);
        return true;
    }

    /**
     * Delete restaurant boolean.
     *
     * @param id the id
     * @throws Exception the exception
     */
    public Boolean deleteRestaurant(final UUID id) throws Exception {
        Restaurant restaurant = (Restaurant) getSession().createCriteria(Restaurant.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        logActivity(ActivityType.ADMIN_DELETE, restaurant.getName() + LOG_DELETE);
        getSession().delete(restaurant);
        return true;
    }

    /**
     * Find restaurants with filter pagination adapter.
     *
     * @param restaurantFilter the restaurant filter
     * @return the pagination adapter
     */
    @SuppressWarnings("unchecked")
    public PaginationAdapter<Restaurant> findRestaurantsWithFilter(final RestaurantFilter restaurantFilter) {
        Criteria criteria = getSession().createCriteria(Restaurant.class);

        if (restaurantFilter.name != null) {
            criteria.add(Restrictions.ilike("name", restaurantFilter.name, MatchMode.ANYWHERE));
        }

        if (restaurantFilter.cityId != null) {
            criteria.add(Restrictions.eq("city.id", restaurantFilter.cityId));
        }

        if (restaurantFilter.price != 0) {
            criteria.add(Restrictions.eq("priceRange", restaurantFilter.price));
        }

        if (restaurantFilter.cuisine != null && restaurantFilter.cuisine[0] != null && !restaurantFilter.cuisine[0].equalsIgnoreCase("")) {
            Criteria cuisine = criteria.createCriteria("cuisines");
            Disjunction disjunction = Restrictions.disjunction();
            for (String singleCuisine : restaurantFilter.cuisine) {
                disjunction.add(Restrictions.eq("name", singleCuisine));
                System.out.println("Current cuisine:" + singleCuisine);
            }
            cuisine.add(disjunction);
        }

        if (restaurantFilter.rating != null && restaurantFilter.rating != 0) {
            List<RestaurantReview> restaurantReviews = getSession()
                    .createSQLQuery("select * FROM restaurant_review " +
                            "GROUP BY restaurant_review.id " +
                            "HAVING avg(rating) >= :averageRatingLow and avg(rating) < :averageRatingHigh")
                    .addEntity(RestaurantReview.class)
                    .setParameter("averageRatingLow", getAverageRatingLow(restaurantFilter.rating))
                    .setParameter("averageRatingHigh", getAverageRatingHigh(restaurantFilter.rating))
                    .list();
            if (restaurantReviews != null) {
                Set<UUID> restaurantsWithRating = new HashSet<>();
                restaurantReviews.stream().filter(Objects::nonNull).forEach(review -> restaurantsWithRating.add(review.getRestaurantId()));
                criteria.add(Restrictions.in("id", restaurantsWithRating));
            }
        }

        Long numberOfPages = ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()) / restaurantFilter.pageSize;

        criteria.setProjection(null)
                .setFirstResult((restaurantFilter.pageNumber - 1) * restaurantFilter.pageSize)
                .setMaxResults(restaurantFilter.pageSize);

        if (restaurantFilter.sortBy.equals("price")) {
            criteria.addOrder(Order.desc("priceRange"));
        }

        criteria.addOrder(Order.asc("name"));

        List<Restaurant> restaurants = criteria.list();

        switch (restaurantFilter.sortBy) {
            case "rating":
                restaurants.sort((o1, o2) -> o2.getAverageRating().compareTo(o1.getAverageRating()));
                break;
        }

        return PaginationAdapter.createOutput()
                .setPageNumber(restaurantFilter.pageNumber)
                .setPageSize(restaurantFilter.pageSize)
                .setModel(restaurants)
                .setNumberOfPages(numberOfPages);
    }

    private double getAverageRatingLow(Integer ratingFilter) {
        switch (ratingFilter) {
            case 1:
                return 0.25;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 4.75;
            default:
                return 0;
        }
    }

    private double getAverageRatingHigh(Integer ratingFilter) {
        switch (ratingFilter) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 4.75;
            case 5:
                return 5.1;
            default:
                return 5.1;
        }
    }

    /**
     * Gets restaurant with id.
     *
     * @param id the id
     * @return the restaurant with id
     */
    public Restaurant getRestaurantWithId(final UUID id) {
        return (Restaurant) getSession().createCriteria(Restaurant.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    /**
     * Gets nearby restaurants.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     * @return the nearby restaurants
     */
    @SuppressWarnings("unchecked")
    public List<Restaurant> getNearbyRestaurants(final Float latitude, final Float longitude) {
        return getSession()
                .createSQLQuery("SELECT * FROM restaurant WHERE restaurant.longitude <> 0 AND restaurant.latitude <> 0 ORDER BY ST_Distance(ST_GeomFromText('POINT(' || restaurant.longitude || ' ' || restaurant.latitude || ')' ,4326), ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')',4326)) ASC LIMIT 3")
                .addEntity(Restaurant.class)
                .setParameter("longitude", longitude)
                .setParameter("latitude", latitude)
                .list();
    }

    /**
     * Gets popular restaurants.
     *
     * @return the popular restaurants
     */
    @SuppressWarnings("unchecked")
    public List<Restaurant> getPopularRestaurants() {

        List<PopularRestaurantsBean> popularRestaurantsBeans = getSession().createCriteria(Reservation.class, "reservation")
                .createAlias("reservation.table", "table")
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("table.restaurantId").as("restaurantId"))
                        .add(Projections.count("table").as("tableCount")))
                .addOrder(Order.asc("tableCount"))
                .setResultTransformer(Transformers.aliasToBean(PopularRestaurantsBean.class))
                .setMaxResults(6)
                .list();

        if (popularRestaurantsBeans.size() > 0) {
            List<UUID> popularRestaurantsIds = popularRestaurantsBeans.stream().map(PopularRestaurantsBean::getRestaurantId).collect(Collectors.toList());

            return (List<Restaurant>) getSession().createCriteria(Restaurant.class)
                    .add(Restrictions.in("id", popularRestaurantsIds))
                    .addOrder(Order.asc("name"))
                    .list();
        }

        return new ArrayList<>();
    }

    /**
     * Gets popular locations.
     *
     * @return the popular locations
     */
    @SuppressWarnings("unchecked")
    public List<PopularLocation> getPopularLocations() {
        List<Object[]> popularLocations = getSession().createCriteria(Restaurant.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("city"))
                        .add(Projections.count("id").as("numberOfRestaurants")))
                .addOrder(Order.desc("numberOfRestaurants"))
                .list();

        return popularLocations.stream().map(PopularLocation::new).collect(Collectors.toList());
    }

    /**
     * Post review boolean.
     *
     * @param reviewForm the review form
     * @param user       the user
     */
    public Boolean postReview(final ReviewForm reviewForm, final User user) {
        RestaurantReview restaurantReview = (RestaurantReview) getSession().createCriteria(RestaurantReview.class)
                .add(Restrictions.eq("restaurantId", reviewForm.getRestaurantId()))
                .add(Restrictions.eq("userId", user.getId()))
                .uniqueResult();
        if (restaurantReview == null) {
            restaurantReview = new RestaurantReview(
                    reviewForm.getRestaurantId(),
                    user.getId(),
                    reviewForm.getReviewScore(),
                    reviewForm.getReviewText()
            );
        } else {
            restaurantReview.setReview(reviewForm.getReviewText());
            restaurantReview.setRating(reviewForm.getReviewScore());
        }
        getSession().save(restaurantReview);
        logActivity(ActivityType.REVIEW_POST, LOG_REVIEW + this.getRestaurantWithId(reviewForm.getRestaurantId()).getName());
        return true;
    }

    /**
     * Gets number of restaurants.
     *
     * @return the number of restaurants
     */
    public Long getNumberOfRestaurants() {
        return Long.valueOf(getSession().createCriteria(Restaurant.class)
                .setProjection(Projections.rowCount())
                .uniqueResult().toString());
    }

    /**
     * Update picture string.
     *
     * @param imageUploadForm the image upload form
     * @return the string
     * @throws Exception the exception
     */
    public String updatePicture(final ImageUploadForm imageUploadForm) throws Exception {
        Restaurant restaurant = (Restaurant) getSession().createCriteria(Restaurant.class)
                .add(Restrictions.eq("id", imageUploadForm.getRestaurantId()))
                .uniqueResult();

        String newImagePath = AWS_BASE_PATH + imageUploadForm.getRestaurantId() + "-" + imageUploadForm.getImageType() + "." + imageUploadForm.getExtension();

        if (imageUploadForm.getImageType().equals("profile")) {
            restaurant.setProfileImagePath(newImagePath);
        } else if (imageUploadForm.getImageType().equals("cover")) {
            restaurant.setCoverImagePath(newImagePath);
        }
        else {
            newImagePath = AWS_BASE_PATH + imageUploadForm.getRestaurantId() + "-" + imageUploadForm.getTimestamp()+ "-" + imageUploadForm.getImageType() + "." + imageUploadForm.getExtension();
            String thumbPath = AWS_BASE_PATH + imageUploadForm.getRestaurantId() + "-" + imageUploadForm.getTimestamp()+ "-" + imageUploadForm.getImageType() + "-thumb" + "." + imageUploadForm.getExtension();
            RestaurantPhoto photo= new RestaurantPhoto();
            photo.setRestaurantId(restaurant.getId());
            photo.setPath(newImagePath);
            photo.setThumb(thumbPath);
            getSession().persist(photo);
        }

        getSession().update(restaurant);
        logActivity(ActivityType.REVIEW_POST, LOG_IMAGE + this.getRestaurantWithId(imageUploadForm.getRestaurantId()).getName());
        return "{ \"imageFor\": \"" + imageUploadForm.getImageType() + "\", \"url\": \"" + newImagePath + "\"}";
    }
}
