'use strict';

angular.module('ec-admin.app', ['ec-admin'])
        .controller('BooksDetailCtrl', [

            '$scope',
            'Util',
            'API',
            '$state',
            'AppConfig',
            '$http',
            '$stateParams',
            '$uibModal',
            '$rootScope',

            function ($scope, Util, API, $state, AppConfig, $http, $stateParams, $uibModal, $rootScope) {
                $scope.book_fields_path = 'js/components/books/book_fields.html';
                $scope.showBntSave = true;
                $scope.submitting = false;
                $scope.tags = [];
                $scope.image = 'img/no-image-available.png';
                $scope.imageUpdate = '';
                $scope.files = [];
                $scope.bindingOptions = ["Paper Back","Hard Binding","Plastic Binding","Spiral Binding"];
                $scope.languagePublishedOptions = ["English","Hindi","Sanskrit","Urdu","Other"];
                $scope.pathFile = AppConfig.PATH_FILE;
                $rootScope.checkTags = [];
//                $scope.fileMedia = [];
//                $scope.multipleImage = [];
                $scope.$watch('files', function () {
                    if($scope.files.length > 0){
                        $scope.origin = false;
                    }
                });
                $scope.paramProduct = {
                    name: "",
                    bookId: 0,
                    companyId: 1,
                    description: "",
                    browsingName: "",
                    defaultImage: "",
                    salePrice: 0,
                    listCategoriesId: [],
                    listPrice: "",
                    overview: "",
                    quantity: 0,
                    isStockControlled: true,
                    sku: "",
                    rank: 0,
                    feedProductType: "bookloader",
                    itemSku: 0,
                    itemName: "",
                    externalProductId: 0,
                    externalProductIdType: "",
                    externalProductInformation: 4901,
                    binding: "Paper Back",
                    edition: "",
                    publicationDate: 2018,
                    authors: ['test auth'],
                    images: [{},{},{}],
                    catalogNumber: 1318095031,
                    genericKeywords: 'Vishv Books',
                    targetAudienceBases: ['Children'],
                    pages: 0,
                    languagePublished: 'English',
                    minimumReadingInterestAge: 3,
                    websiteShippingWeight: '0 gm',
                    itemHeight: '0 cm',
                    itemLength: '0 cm',
                    itemWidth: '0 cm',
                    itemWeight: '0 cm',
                    countryOfOrigin: 'India',
                    legalDisclaimerDescription: 'All disputes are subject to Delhi Court Jurisdiction only.'
                };
                $scope.uploadFile = {
                    file_name: "",
                    file: {},
                    object_type: "",
                    object_sub_type: "",
                    object_id: 0,
                    name: "",
                    chunk: "",
                    chunks: ""
                };

                $scope.getProductInfo = function (cb) {
                    Util.createRequest(API.DETAIL_BOOK.path + $stateParams.id, function (response) {
                        var status = response.status;
                        if (status === 200) {
                            $scope.tags = response.data.list_category;
                            var tagsCheck = angular.copy($scope.tags);
                            $scope.$watch('tags', function () {
                                $rootScope.checkTags = $scope.tags;
                                $scope.origin = angular.equals($scope.tags, tagsCheck);
                            }, true);
//                            $scope.imageUpdate = response.data.product.defaultImage;
                            $scope.product = response.data.product;
                            if ($scope.product.defaultImage !== 'img/no-image-available.png') {
                                $scope.product.defaultImage = $scope.pathFile + $scope.product.defaultImage;
                            }
                            var originCar = angular.copy($scope.product);
                            $scope.$watch('product', function () {
                                $scope.origin = angular.equals($scope.product, originCar);
                            }, true);
                            if (cb) {
                                cb && cb();
                            }
                        } else {
                            Util.showErrorAPI(status);
                        }
                    }).finally(function () {
                        $scope.submitting = false;
                    });
                };

                $scope.$watchCollection('files', function (newVal) {
                    if (newVal) {
                        $scope.origin = false;
                    }
                });

                $scope.updateProduct = function () {
                    $scope.paramProduct.bookId = $stateParams.id;
                    $scope.paramProduct.browsingName = $scope.product.browsingName;
                    $scope.paramProduct.description = $scope.product.description;
                    $scope.paramProduct.name = $scope.product.name;
                    $scope.paramProduct.quantity = $scope.product.quantity;
                    $scope.paramProduct.overview = $scope.product.overview;
                    $scope.paramProduct.sku = $scope.product.sku;
                    $scope.paramProduct.salePrice = $scope.product.salePrice;
                    $scope.paramProduct.defaultImage = $scope.imageUpdate;
                    $scope.tags.forEach(function (result) {
                        $scope.paramProduct.listCategoriesId.push(result.id);
                    });
                    $scope.showBntSave = false;
                    $scope.submitting = false;
                    if ($scope.files.length > 0) {
                        $scope.paramProduct.defaultImage = $scope.files[0].name;
                    }
                    Util.createRequest(API.UPDATE_BOOK, $scope.paramProduct, function (response) {
                        var status = response.status;
                        if (status === 200) {
                            Util.showSuccessToast('message.product.update_product_success');
                            // goto car branch list
                            $state.go('books.list');
                        } else {
                            Util.showErrorToast('message.product.update_product_error');
                        }
                    }).finally(function () {

                    });

                };

                ///targetAudienceBasesOptions
                $scope.targetAudienceBasesOptions = ['Children', 'Adult', 'Student', 'Senior Citizen'];
                $scope.toggleTargetSelection = function (opt) {
                    var idx = $scope.product.targetAudienceBasesOptions.indexOf(opt);

                    // Is currently selected
                    if (idx > -1) {
                        $scope.product.targetAudienceBasesOptions.splice(idx, 1);
                    }

                    // Is newly selected
                    else {
                        $scope.product.targetAudienceBasesOptions.push(opt);
                    }
                };

                //author
                $scope.addAuthor = function(author){
                    if(author){
                        $scope.paramProduct.authors.push(author);
                        $scope.author = null;
                    }
                };

                $scope.removeAuthor = function(author){
                    var index = $scope.paramProduct.authors.indexOf(author);
                    $scope.paramProduct.authors.splice(index, 1);
                };

                $scope.dropifyObj = [];

                $scope.loadDropify = function (class_name) {
                    setTimeout(function () {
                        var obj = $('.'+class_name).dropify({
                            messages: {
                                'default': 'Drag and drop a file here or click',
                                'replace': 'Drag and drop or click to replace',
                                'remove':  'Remove',
                                'error':   'Ooops, something wrong happended.'
                            }
                        });

                        obj.on('dropify.beforeClear', function(event, element){
                            var className = element.element.className;
                            var index = className.replace('dropify','');
                            //console.log('className',className);
                            //console.log('index',index);
                            $scope.paramProduct.images[index] = {};
                        });


                    },200);
                };



                // Pop-up
                $scope.addCategory = function () {
                    var modalInstance = $uibModal.open({
                        templateUrl: 'js/components/template/confirm_category_modal.html',
                        windowClass: 'large-Modal',
                        controller: ['$scope', '$uibModalInstance', 'Util', '$rootScope', function ($scope, $uibModalInstance, Util, $rootScope) {
                                $scope.searchString = "";
                                $scope.title = Util.translate('table.header.pop_up.list_cate');
                                $scope.message = "";
                                $scope.listCategory = [];
                                $scope.curentSelected = [];
                                $scope.pagination = {};
                                $scope.opts = {
                                    lblAccept: Util.translate('button.add'),
                                    lblDismiss: Util.translate('button.cancel')
                                };
                                $scope.maxSize = 5;
                                $scope.currentPage = 1;
                                $scope.pageSize = 10;
                                $scope.pageChanged = function () {
                                    $scope.loadListSearch();
                                };
                                var params = {
                                    company_id: 1,
                                    search_key: "",
                                    sort_key: 1,
                                    page_size: 100,
                                    page_number: 1
                                };

                                Util.createRequest(API.LIST_CATEGORY, params, function (response) {
//                                    console.log($scope.product.listCategoriesId);
                                    var status = response.status;
                                    if (status === 200) {
                                        $scope.listCategory = response.data.content;
                                        $scope.listCategory.forEach(function (result) {
                                            result.selected = false;
                                            $rootScope.checkTags.forEach(function(check){
                                                if(check.id === result.categoryId){
                                                    $scope.curentSelected.push(result);
                                                }
                                            });
                                        });
                                        $scope.removeSelected($scope.curentSelected);
                                        var originCar = angular.copy($scope.listCategory);
                                        $scope.$watch('listCategory', function () {
                                            $scope.origin = angular.equals($scope.listCategory, originCar);
                                        }, true);
//                                        $scope.totalItems = response.data.totalResult;
//                                        $scope.totalPage = response.data.totalPage;
                                    } else {
                                        Util.showErrorAPI(status);
                                    }
                                });

                                $scope.loadListSearch = function () {
                                    params.search_key = $scope.searchString;
                                    Util.createRequest(API.LIST_CATEGORY, params, function (response) {

                                        var status = response.status;
                                        if (status === 200) {
                                            $scope.listCategory = response.data.content;
                                            $scope.listCategory.forEach(function (result) {
                                                result.selected = false;
                                            });
                                        } else {
                                            Util.showErrorAPI(status);
                                        }
                                    });
                                };

                                $scope.removeSelected = function (list) {
                                    list.forEach(function (item) {
                                        var index = $scope.listCategory.indexOf(item);
                                        $scope.listCategory.splice(index, 1);
                                    });
                                    $scope.curentSelected = [];
                                };
                                
                                $scope.onClose = function () {
                                    $uibModalInstance.close();
                                };

                                $scope.onDismiss = function () {
                                    $uibModalInstance.close();
                                };

                                $scope.onAccept = function () {
                                    var chooseCate = [];
                                    $scope.listCategory.forEach(function (result) {
                                        if (result.selected) {
                                            chooseCate.push(result);
                                        }
                                    });
                                    $rootScope.$broadcast('greeting', {any: chooseCate});
                                    $uibModalInstance.close();

                                };
                            }]
                    });
                    return modalInstance;
                };

                $scope.$on('greeting', listenGreeting);

                function listenGreeting($event, response) {
                    if (response.any.length > 0) {
                        response.any.forEach(function (result) {
                            var text = {
                                text: result.name,
                                id: result.categoryId
                            };
                            $scope.tags.push(text);
                        });
                    }
                }
//                $scope.$watch('tags', function () {
//                    $scope.tags.forEach(function(result){
//                        $scope.paramProduct.listCategoriesId.push(result.id);
//                    });
//                }, true);
                $scope.loadTags = function (query) {
                    return $http.get('/tags?query=' + query);
                };

                $scope.openPopUp = function () {
                    $scope.addCustomer();
                };

                function innit() {
                    $scope.getProductInfo();
                }
                ;

                innit();
            }
        ]);