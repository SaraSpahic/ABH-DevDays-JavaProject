import Ember from 'ember';

export default Ember.Route.extend({
  ajax: Ember.inject.service(),
  model() {
    return Ember.RSVP.hash({
      statistics: this.get('ajax').request('/admin/getStatistics', {
        xhrFields: {
          withCredentials: true,
        }
      }),
      acitvitylogs: this.get('ajax').request('/admin/getActivityLogs', {
          xhrFields: {
            withCredentials: true,
          }
      }),
});
}
});
