package adapters.inbound.rest.dto

import application.entities.SocialMedia

class ScheduleSearchRequest {
    var search: String? = null
    var ids: String? = null
    var socialMedia: SocialMedia? = null
}
