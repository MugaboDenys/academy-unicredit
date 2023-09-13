/* global jQuery, Coral */
(function($, Coral) {
    "use strict";

    console.log(" I can load this clientLibs ");

    var registry = $(window).adaptTo("foundation-registry");

    registry.register("foundation.validation.validator", {
        selector: "[data-validation=abstract]",
        validate: function(element) {
            let el = $(element);
            if(el.length>3){
               return "Please add at least 10 characters in abstract";
            }

        }
    });


})(jQuery, Coral);
