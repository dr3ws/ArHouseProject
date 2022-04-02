(function ($) {
    $(document).ready(function () {
        "use strict";

        // HOVER TOGGLE
        $('.side-navigation .menu ul li a').on('click', function (e) {
            $(this).parent().children('.side-navigation .menu ul li ul').slideToggle(300);
            return true;
        });

        // CONTACT FORM INPUT LABEL
        function checkForInput(element) {
            const $label = $(element).siblings('span');
            if ($(element).val().length > 0) {
                $label.addClass('label-up');
            } else {
                $label.removeClass('label-up');
            }
        }

        $('input, textarea').each(function (e) {
            checkForInput(this);
        });

        $('input, textarea').on('change keyup', function (e) {
            checkForInput(this);
        });

        // TOOLTIP
        // $('[data-toggle="tooltip"]').tooltip();

        // PARALLAX
        $.stellar({
            horizontalScrolling: false,
            verticalOffset: 0,
            responsive: true
        });

        // HAMBURGER
        $('.hamburger').on('click', function (e) {
            $(this).toggleClass('open');
            $('body').toggleClass('overflow');
            $('.side-navigation').toggleClass('active');
        });

        // DATA BACKGROUND IMAGE
        let pageSection = $("*");
        pageSection.each(function (indx) {
            if ($(this).attr("data-background")) {
                $(this).css("background-image", "url(" + $(this).data("background") + ")");
            }
        });

        // PAGE TRANSITION
        $('body a').on('click', function (e) {
            if (typeof $(this).data('fancybox', 'filter') == 'undefined') {
                e.preventDefault();
                let url = this.getAttribute("href");
                if (url.indexOf('#') !== -1) {
                    let hash = url.substring(url.indexOf('#'));


                    if ($('body ' + hash).length !== 0) {
                        $('.transition-overlay').removeClass("active");
                        $(".hamburger").toggleClass("open");
                        $(".navigation-menu").removeClass("active");


                        $('html, body').animate({
                            scrollTop: $(hash).offset().top
                        }, 1300);

                    }
                } else {
                    $('.transition-overlay').toggleClass("active");
                    setTimeout(function () {
                        window.location = url;
                    }, 1300);

                }
            }
        });
    });

    // COUNTER
    $(document).scroll(function () {
        $('.odometer').each(function () {
            let parent_section_postion = $(this).closest('section').position();
            let parent_section_top = parent_section_postion.top;
            if ($(document).scrollTop() > parent_section_top - 300) {
                if ($(this).data('status') === 'yes') {
                    $(this).html($(this).data('count'));
                    $(this).data('status', 'no')
                }
            }
        });
    });

    // SLIDER
    let SLIDER_CONTAINER = document.querySelector('.slider-container');
    if (SLIDER_CONTAINER) {
        let swiper_sldr = new Swiper(SLIDER_CONTAINER, {
            touchRatio: 0,
            loop: true,
            speed: 600,
            autoplay: {
                delay: 4500,
                disableOnInteraction: false,
            },
            pagination: {
                el: '.pagination',
                type: 'fraction',
            },
            navigation: {
                nextEl: '.button-next',
                prevEl: '.button-prev',
            },
        });
    }

    let ISOTOPE_GALLERY = document.querySelector('.gallery');
    if (ISOTOPE_GALLERY) {
        // MASONRY
        $(window).load(function () {
            $(ISOTOPE_GALLERY).isotope({
                itemSelector: '.gallery li',
                percentPosition: true
            });
        });

        // ISOTOPE FILTER
        let $container = $(ISOTOPE_GALLERY);
        $container.isotope({
            filter: '*',
            animationOptions: {
                duration: 750,
                easing: 'linear',
                queue: false
            }
        });
    }

    // WOW ANIMATION
    let WOW_ANIMATION = document.querySelector('.wow');
    if (WOW_ANIMATION) {
        let wow = new WOW({
            boxClass: 'wow', // default
            animateClass: 'animated', // default
            offset: 100, // default
            mobile: true, // default
            live: true // default
        })
        wow.init();
    }

    // PRELOADER
    $(window).load(function () {
        $("body").addClass("page-loaded");
    });
})(jQuery);

$('#uploaded-file-img').bind('change', function () {
    let check = 0;
    let size = this.files[0].size;
    let name = this.files[0].name;

    if (1000000 < size) {
        alert("Размер файла не должен превышать 1 МБ");
        check = 1;
    }

    if (check !== 1) {
        let fileExtension = ['jpeg', 'jpg', 'png', 'bmp']; // допустимые типы файлов
        if ($.inArray(name.split('.').pop().toLowerCase(), fileExtension) === -1) {
            alert("Загрузите пожалуйста изображение");
            check = 1;
        }
    }

    if (check !== 1) {
        try {
            let file = document.getElementById('uploaded-file-img').files[0];

            if (file) {
                document.getElementById('file-name').innerHTML = file.name;

                if (/\.(jpe?g|bmp|png)$/i.test(file.name)) {
                    let elPreview = document.getElementById('preview');
                    elPreview.innerHTML = '';
                    let newImg = document.createElement('img');
                    newImg.className = "preview-img";

                    let reader = new FileReader();
                    reader.onloadend = function (event) {
                        if (event.target.readyState === FileReader.DONE) {
                            newImg.setAttribute('src', event.target.result.toString());
                            elPreview.appendChild(newImg);
                        }
                    };

                    let blob;
                    if (file.slice)
                        blob = file.slice(0, file.size);
                    // } else if (file.webkitSlice) {
                    // 	blob = file.webkitSlice(0, file.size);
                    // } else if (file.mozSlice) {
                    // 	blob = file.mozSlice(0, file.size);
                    // }
                    reader.readAsDataURL(blob);
                }
            }
        } catch (e) {
            let file = document.getElementById('uploaded-file-img').value;
            file = file.replace(/\\/g, "/").split('/').pop();
            document.getElementById('file-name').c.innerHTML = file;
        }
    }
});