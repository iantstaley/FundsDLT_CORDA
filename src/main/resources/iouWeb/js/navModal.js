"use strict";

// Similar to the IOU creation modal - see createIOUModal.js for comments.
angular.module('demoAppModule').controller('navModalCtrl', function ($http, $uibModalInstance, $uibModal, apiBaseURL, peers, id) {
    const navModal = this;

    navModal.peers = peers;
    navModal.id = id;
    navModal.form = {};
    navModal.formError = false;

    navModal.saveNav = () => {
        if (invalidFormInput()) {
            navModal.formError = true;
        } else {
            navModal.formError = false;

            const id = navModal.id;
            const navValue = navModal.form.navValue;

            $uibModalInstance.close();

            const issueIOUEndpoint =
                apiBaseURL +
                `saveNav?id=${id}&value=${navValue}&`;

            $http.get(issueIOUEndpoint).then(
                (result) => transferModal.displayMessage(result),
                (result) => transferModal.displayMessage(result)
            );
        }
    };

    transferModal.displayMessage = (message) => {
        const transferMsgModal = $uibModal.open({
            templateUrl: 'transferMsgModal.html',
            controller: 'transferMsgModalCtrl',
            controllerAs: 'transferMsgModal',
            resolve: { message: () => message }
        });

        transferMsgModal.result.then(() => {}, () => {});
    };

    transferModal.cancel = () => $uibModalInstance.dismiss();

    function invalidFormInput() {
        return false;
    }
});

angular.module('demoAppModule').controller('transferMsgModalCtrl', function ($uibModalInstance, message) {
    const transferMsgModal = this;
    transferMsgModal.message = message.data;
});