(function () {
    if (window.userUtils !== undefined) {
        return;
    }

    const userUtils = {};

    userUtils.store = {
        users: [],
        userViews: [],
        editUser: null,
    };

    userUtils.templates = {};


    userUtils.init = function () {
        userUtils.templates.record = document.querySelector('#template-record').content;

        const createRecordView = document.body.querySelector('.edit-user-wrapper');
        userUtils.store.createRecordView = createRecordView;
        createRecordView.style.display = 'none';

        createRecordView.querySelector('.edit-user-cancel-btn')
            .addEventListener('click', () => {
                userUtils.spinner.fullScreenOverlay.style.display = 'none';
                userUtils.store.createRecordView.style.display = 'none';
            });

        createRecordView.querySelector('.edit-user-save-btn')
            .addEventListener('click', function () {
                userUtils.spinner.fullScreenOverlay.style.display = 'none';
                userUtils.store.createRecordView.style.display = 'none';
                const firstName = document.body.querySelector('.edit-user-first-name').value;
                const lastName = document.body.querySelector('.edit-user-last-name').value;

                let user = {
                    firstName: firstName,
                    lastName: lastName,
                };
                if (userUtils.store.editUser) {
                    const editUser = userUtils.store.editUser;
                    user.id = editUser.id;
                    userUtils.ajax
                        .updateUser(user)
                        .then(
                            (data) => {
                                userUtils.user.refreshTable();
                            })
                        .catch((err) => {
                            console.error(err);
                            showModal(err)
                        });
                } else {
                    userUtils.ajax
                        .saveUser(user)
                        .then(
                            (data) => {
                                userUtils.user.refreshTable();
                            })
                        .catch((err) => {
                            console.error(err);
                            showModal(err)
                        });
                }
            });
        createRecordView.querySelector('.user-account-info-btn')
            .addEventListener('click', function (event) {
                const accountId = document.body.querySelector('.edit-user-account-id').value;
                if (accountId) {
                    userUtils.ajax.accountInfo(accountId);
                }
            });
        document.body.querySelector('.edit-user-add-btn')
            .addEventListener('click', function (event) {
                const userId = userUtils.store.editUser.id;
                userUtils.ajax.addAccount(userId);
            });

        document.body.querySelector('.create-user')
            .addEventListener('click', () => {
                document.body.querySelector('.edit-user-add-btn').style.display = 'none';
                document.body.querySelector('.user-account-info-btn').style.display = 'none';
                userUtils.user.createNew(false);
            });
        document.body.querySelector('.refresh-table')
            .addEventListener('click', () => {
                userUtils.user.refreshTable();
            });

        userUtils.store.userTable = document.querySelector('.users-table-content');
        userUtils.store.modal = document.getElementById("myModal");
        userUtils.store.span = document.getElementsByClassName("close")[0];
    };


    userUtils.spinner = {};
    userUtils.spinner.spinnerContainerClassName = 'users-spinner-container';
    userUtils.spinner.spinnerClassName = 'users-spinner';

    userUtils.spinner.createSpinner = function () {
        if (document.body.querySelector(`.${this.spinnerContainerClassName}`)) {
            return;
        }

        this.fullScreenOverlay = document.querySelector('.full-screen-overlay');
        this.fullScreenOverlay.style.display = 'none';
        const spinnerContainer = document.createElement('div');
        spinnerContainer.classList.add(this.spinnerContainerClassName);

        const spinner = document.createElement('div');
        spinner.classList.add(this.spinnerClassName);
        spinnerContainer.append(spinner);

        spinner.innerHTML = ` <div class="sk-grid">
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                               </div>
                             `;
        document.body.prepend(spinnerContainer);
        userUtils.spinner.spinnerContainer = spinnerContainer;
    };

    userUtils.spinner.showSpinner = function () {
        this.fullScreenOverlay.style.display = '';
        this.spinnerContainer.style.display = '';
    };
    userUtils.spinner.hideSpinner = function () {
        this.fullScreenOverlay.style.display = 'none';
        this.spinnerContainer.style.display = 'none';
    };


    userUtils.ajax = {
        baseUrl: '',
        get usersUrl() {
            return this.baseUrl + '/users';
        },
        get accountsUrl() {
            return this.baseUrl + '/accounts';
        }
    };

    userUtils.ajax.getUsers = function () {
        userUtils.spinner.showSpinner();
        return window.axios
            .get(this.usersUrl)
            .then((response) => {
                userUtils.store.users = response.data;
                userUtils.spinner.hideSpinner();
            })
            .catch((err) => {
                console.error(err);
                showModal(err);
                userUtils.spinner.hideSpinner();
            });
    };

    userUtils.ajax.findUserById = function (id) {
        return window.axios.get(`${this.usersUrl}/${id}`)
    };

    userUtils.ajax.findAccountByUserId = function (userId) {
        return window.axios.get(`${this.accountsUrl}/user/${userId}`)
    };

    userUtils.ajax.deleteUserById = function (id) {
        return window.axios.delete(`${this.usersUrl}/${id}`);
    };

    function showModal(err) {
        let modalMessage = userUtils.store.modal.querySelector('.text');
        let errorData = err.response.data;
        if (errorData.hasOwnProperty("message")) {
            modalMessage.innerHTML = errorData.message;
        } else {
            modalMessage.innerHTML = JSON.stringify(errorData);
        }
        userUtils.store.modal.style.display = "block";
        userUtils.store.span.onclick = function () {
            userUtils.store.modal.style.display = "none";
        }
    }

    userUtils.ajax.saveUser = function (user) {
        return window.axios.post(`${this.usersUrl}`, user);
    };

    userUtils.ajax.updateUser = function (user) {
        return window.axios.put(`${this.usersUrl}/${user.id}`, user);
    };

    userUtils.user = {};
    userUtils.user.refreshTable = function () {
        userUtils.ajax.getUsers().then((data) => {
            userUtils.user.cleanTable();
            userUtils.user.fillTable();
        })
    };

    userUtils.user.edit = function (user) {
        document.body.querySelector('.user-account-info-btn').style.display = 'none';
        document.body.querySelector('.edit-user-add-btn').style.display = 'none';
        userUtils.ajax.findAccountByUserId(user.id)
            .then((response) => {
                document.body.querySelector('.user-account-info-btn').style.display = '';
                let account = response.data;
                userUtils.user.createNew(true, user, account);
            })
            .catch((err) => {
                document.body.querySelector('.edit-user-add-btn').style.display = '';
                userUtils.user.createNew(true, user);
            });


    };

    userUtils.ajax.accountInfo = function (accountId) {
        location.href = `${this.accountsUrl}/${accountId}/info`;
    };
    userUtils.ajax.addAccount = function (userId) {
        location.href = `${this.usersUrl}/${userId}/addAccount`;
    };

    userUtils.user.delete = function (user) {
        userUtils.spinner.showSpinner();
        userUtils.ajax.deleteUserById(user.id)
            .then((response) => {
                userUtils.spinner.hideSpinner();
                userUtils.user.refreshTable();
            })
            .catch((err) => {
                console.error(err);
                showModal(err);
                userUtils.spinner.hideSpinner();
            });
    };

    userUtils.user.createRecord = function (user) {
        const viewTemplate = userUtils.templates.record;
        viewTemplate.querySelector('.user-first-name').innerText = user.firstName;
        viewTemplate.querySelector('.user-last-name').innerText = user.lastName;
        let view = document.importNode(viewTemplate, true);
        view.userId = user.id;

        view.querySelector('.user-edit-btn')
            .addEventListener('click', function (event) {
                userUtils.user.edit(user)
            });
        view.querySelector('.user-delete-btn')
            .addEventListener('click', function () {
                userUtils.user.delete(user);
            });

        userUtils.store.userViews.push(view);

        return view;
    };

    userUtils.user.createNew = function (edit, user, account) {
        userUtils.store.createRecordView.style.display = '';
        userUtils.spinner.fullScreenOverlay.style.display = '';
        const firstName = document.body.querySelector('.edit-user-first-name');
        const lastName = document.body.querySelector('.edit-user-last-name');
        const accountId = document.body.querySelector('.edit-user-account-id');
        const accountAmount = document.body.querySelector('.edit-user-account-amount');
        if (edit) {
            userUtils.store.editUser = user;
            firstName.value = user.firstName;
            lastName.value = user.lastName;
            if (account) {
                accountId.value = account.id;
                accountAmount.value = account.amount;
            } else {
                accountId.value = null;
                accountAmount.value = null;
            }
        } else {
            firstName.value = '';
            lastName.value = '';
            accountId.value = null;
            accountAmount.value = null;
            userUtils.store.editUser = null;
        }
    };

    userUtils.user.fillTable = function () {
        userUtils.store.users.forEach((u) => {
            userUtils.store.userTable.append(userUtils.user.createRecord(u));
        })
    };

    userUtils.user.cleanTable = function () {
        userUtils.store.userTable.innerHTML = '';
    };

    window.userUtils = userUtils;
}());
