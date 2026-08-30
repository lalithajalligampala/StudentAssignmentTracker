self.addEventListener("install", function(event) {

    console.log("Notification service worker installed.");

    self.skipWaiting();

});


self.addEventListener("activate", function(event) {

    console.log("Notification service worker activated.");

    event.waitUntil(
        self.clients.claim()
    );

});


/*
 * Handle notification clicks
 */
self.addEventListener("notificationclick", function(event) {

    console.log("Notification clicked.");

    event.notification.close();

    event.waitUntil(

        self.clients.matchAll({
            type: "window",
            includeUncontrolled: true
        }).then(function(clientList) {

            /*
             * If the application is already open,
             * focus the existing window.
             */

            for (var i = 0; i < clientList.length; i++) {

                var client = clientList[i];

                if ("focus" in client) {

                    return client.focus();

                }

            }

            /*
             * Otherwise open the application.
             */

            return self.clients.openWindow(
                "/StudentAssignmentTracker/"
            );

        })

    );

});