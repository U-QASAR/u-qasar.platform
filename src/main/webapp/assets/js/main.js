/**
 * 
 */
function initCollapse() {
	$('.collapse').collapse({
		toggle : false
	});
}

/**
 * use bootstrap collapse to toggle elements which match given selector. 
 */
function toggleCollapse(selector) {
	$(selector).collapse('toggle');
}


/**
 * use bootstrap collapse to hide elements which match given selector. 
 */
function closeCollapse(selector) {
	if ($(selector).hasClass('in')) {
		$(selector).collapse('hide');
	}
}

/**
 * use bootstrap collapse to show elements which match given selector. 
 */
function showCollapse(selector) {
	if (!$(selector).hasClass('in')) {
		$(selector).collapse('show');
	}
}

/**
 * appends an item at the end of the given container.
 */
function appendElemToContainer(itemType, itemId, containerId) {
	var item = document.createElement(itemType);
	item.id = itemId;
	Wicket.$(containerId).appendChild(item);
}

/**
 * prepends an item at the beginning of the given container.
 */
function prependElemToContainer(itemType, itemId, containerId) {
	var item = document.createElement(itemType);
	item.id = itemId;
	var container = Wicket.$(containerId);
	var firstElem = container.firstChild;
	if (firstElem) {
		container.insertBefore(item, firstElem);
	} else {
		container.appendChild(item);
	}
};

/**
 * fades in the elem with the given id.
 * @param id
 */
function fadeInElem(id) {
	$('#' + id).fadeIn(500);
};

/**
 * fades out the elem with the given id
 * @param id
 * @returns {Boolean}
 */
function fadeOutElem(id, notifyCallback) {
	$('#' + id).fadeOut(500, notifyCallback);
};

/**
 * fades out the elem with the given id and removes it from DOM
 * @param id
 * @returns {Boolean}
 */
function fadeOutAndRemoveElem(id, notifyCallback) {
	$('#' + id).fadeOut(500, function() {
		$('#' + id).remove();
		notifyCallback();
	});
};

/**
 * slides up the elem with the given id
 * @param id
 * @returns {Boolean}
 */
function slideUp(id, notifyCallback) {
	$('#' + id).slideUp(500, notifyCallback);
}

/**
 * slides down out the elem with the given id
 * @param id
 * @returns {Boolean}
 */
function slideDown(id, notifyCallback) {
	$('#' + id).slideDown(500, notifyCallback);
}

/**
 * 
 */
function activateTableSort(id) {
	jQuery('#' + id).tablesorter({ 
        headers: { 
            0: { sorter: false }, 
            4: { sorter: false },
            5: { sorter: false },
            6: { sorter: false },
            7: { sorter: false },
            8: { sorter: false } 
        } 
    }); 
};

function gotoHashTab(tabsContainerId, initialTabId, customHash) {
    var hash = customHash || location.hash;
    var hashPieces = hash.split('?');
    var hashPiece = hashPieces[0] || initialTabId;
    var activeTab = $(tabsContainerId + ' [href=' + hashPiece + ']');
    activeTab && activeTab.tab('show');
    if (!customHash) {
        setTimeout(function() {
            scrollTo(0, 0);
        }, 1);
    }
};

function activateTabHashing(tabsContainerId, initialTabId) {
    // when the nav item is selected update the page hash
    $('.nav a').on('shown', function(e) {
        var scrollmem = $('body').scrollTop();
        window.location.hash = e.target.hash;
        $('html,body').scrollTop(scrollmem);
    });

    // when a link within a tab is clicked, go to the tab requested
    $('.tab-pane a').click(function(event) {
        if (event.target.hash) {
            gotoHashTab(tabsContainerId, initialTabId, event.target.hash);
        }
    });
    
    gotoHashTab(tabsContainerId, initialTabId);
};

function usingPhone() {
    return determineDeviceVisibility('phone');
}

function usingTablet() {
    return determineDeviceVisibility('tablet');
}

function usingDesktop() {
    return determineDeviceVisibility('desktop');
}

function determineDeviceVisibility($device) {
    var $field = $('<div class="visible-' + $device + '"></div>');
    $('body').append($field);
    var $visible = $field.is(':visible');
    $field.remove();
    return $visible;
}
