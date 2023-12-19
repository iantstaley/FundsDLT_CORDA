"use strict";

// Similar to the IOU creation modal - see createIOUModal.js for comments.
angular.module('demoAppModule').controller('IssueCashModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL,id) {
    const issueCashModal = this;
     issueCashModal.id = id;
    issueCashModal.form = {};
    issueCashModal.formError = false;

    issueCashModal.issue = () => {
        if (invalidFormInput()) {
            issueCashModal.formError = true;
        } else {
            issueCashModal.formError = false;

            const Amount = issueCashModal.form.amount;
            //const currency = issueCashModal.form.currency;

            $uibModalInstance.close();

            const issueCashEndpoint =
                apiBaseURL +
                `amountpaid?id=${id}&amount=${Amount}`;

            $http.get(issueCashEndpoint).then(
                (result) => {console.log(result.toString()); issueCashModal.displayMessage(result); },
                (result) => {console.log(result.toString()); issueCashModal.displayMessage(result); }
            );
        }
    };

    issueCashModal.displayMessage = (message) => {
        const issueCashMsgModal = $uibModal.open({
            templateUrl: 'issueCashMsgModal.html',
            controller: 'issueCashMsgModalCtrl',
            controllerAs: 'issueCashMsgModal',
            resolve: {
                message: () => message
            }
        });

        issueCashMsgModal.result.then(() => {}, () => {});
    };

    issueCashModal.cancel = () => $uibModalInstance.dismiss();

    function invalidFormInput() {
        return false;
    }
});

angular.module('demoAppModule').controller('issueCashMsgModalCtrl', function($uibModalInstance, message) {
    const issueCashMsgModal = this;
    issueCashMsgModal.message = message.data;
});