package models.helpers;

import exceptions.ServiceException;

import java.util.UUID;

/**
 * The type Restaurant filter.
 */
public class RestaurantFilter {
    /**
     * The Page number.
     */
    public Integer pageNumber = 1;
    /**
     * The Page size.
     */
    public Integer pageSize = 9;

    /**
     * The Name.
     */
    public String name;
    /**
     * The City id.
     */
    public UUID cityId;
    /**
     * The Sort by.
     */
    public String sortBy;

    /**
     * The Price.
     */
    public int price;

    /**
     * The Rating.
     */
    public Integer rating;


    /**
     * The Cuisine.
     */
    public String[] cuisine;

    private RestaurantFilter() {
    }

    /**
     * Create filter restaurant filter.
     *
     * @return the restaurant filter
     */
    public static RestaurantFilter createFilter() {
        return new RestaurantFilter();
    }

    /**
     * Sets page number.
     *
     * @param pageNumber the page number
     * @return the page number
     * @throws ServiceException the service exception
     */
    public RestaurantFilter setPageNumber(Integer pageNumber) throws ServiceException {
        if (pageNumber <= 0) {
            throw new ServiceException("Page Number must be a Positive Integer");
        } else {
            this.pageNumber = pageNumber;
        }
        return this;
    }

    /**
     * Sets page size.
     *
     * @param pageSize the page size
     * @return the page size
     * @throws ServiceException the service exception
     */
    public RestaurantFilter setPageSize(Integer pageSize) throws ServiceException {
        if (pageSize <= 0) {
            throw new ServiceException("Page Size must be a Positive Integer");
        } else {
            this.pageSize = pageSize;
        }
        return this;
    }

    /**
     * Sets name filter.
     *
     * @param name the name
     * @return the name filter
     */
    public RestaurantFilter setNameFilter(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets city filter.
     *
     * @param cityId the city id
     * @return the city filter
     * @throws ServiceException the service exception
     */
    public RestaurantFilter setCityFilter(UUID cityId) throws ServiceException {
        this.cityId = cityId;
        return this;
    }

    /**
     * Sets price filter.
     *
     * @param price the price
     * @return the price filter
     * @throws ServiceException the service exception
     */
    public RestaurantFilter setPriceFilter(int price) throws ServiceException {
        this.price = price;
        return this;
    }


    /**
     * Sets rating filter.
     *
     * @param rating the rating
     * @return the rating filter
     * @throws ServiceException the service exception
     */
    public RestaurantFilter setRatingFilter(Integer rating) throws ServiceException {
        this.rating = rating;
        return this;
    }


    /**
     * Sets cuisine filter.
     *
     * @param cuisine the cuisine
     * @return the cuisine filter
     * @throws ServiceException the service exception
     */
    public RestaurantFilter setCuisineFilter(String[] cuisine) throws ServiceException {
        this.cuisine = cuisine;
        return this;
    }

    /**
     * Sets sort.
     *
     * @param sortBy the sort by
     * @return the sort
     */
    public RestaurantFilter setSort(String sortBy) {
        if (sortBy != null && !sortBy.equals("")) {
            this.sortBy = sortBy.toLowerCase();
        } else {
            this.sortBy = "name";
        }
        return this;
    }
}
