/*
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of Fenix IST.
 *
 * Fenix IST is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fenix IST is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.
 */
var h = [];

function selectGrouping(groupingId) {
	execute(function(){
		$('#groupings').hide();
		$('#shifts').show();
		$('#students').show();
		
		$('[grouping]').hide();
		$('[grouping='+ groupingId +']').show();
	});
}

function selectShift(shiftId) {
	execute(function() {
		$('#groupings').fadeOut();
		$('#shifts').fadeIn();
		$('#students').fadeIn();
		
		$('[student-group]').fadeIn();
		if(shiftId) {
			$('[shift]').fadeOut();
			$('[shift='+ shiftId +']').fadeIn();
		} else {
			$('[shift]').fadeIn();
		}
	});
}

function selectStudentGroup(groupId) {
	if ( $("#shifts").is(":visible") ) {
	    studentsGroup(groupId);
	} else { 
		studentsOnly(groupId);
	}
}

function studentsGroup(groupId) {
	execute(function() {
		$('#groupings').fadeOut();
		$('#shifts').fadeIn();
		$('#students').fadeIn();
		if(groupId) {
			$('[student-group]').fadeOut();
			$('[student-group=' + groupId + ']').fadeIn();
		} else {
			$('[student-group]').fadeIn();
		}
	});
}

function studentsOnly(groupId) {
	execute(function() {
		$('#groupings').fadeOut();
		$('#shifts').fadeOut();
		$('#students').fadeIn();
		if(groupId) {
			$('[student-group]').fadeOut();
			$('[student-group=' + groupId + ']').fadeIn();
		} else {
			$('[student-group]').fadeIn();
		}
	});
}

function restart() {
	h = [];
	$('section').hide();
	$('#groupings').show();
}

function execute(fn) {
	fn();
	h.push(fn);
}

function back() {
	if(h.length > 1) {
		h.pop();
		var method = h.pop();
		method();
	} else {
		console.log("restart",h);
		restart();
	}
}