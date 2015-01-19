/**
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
package pt.utl.ist.codeGenerator.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.DateTime;

public class ExamRoomManager extends HashSet<Space> {

    private final Map<ExecutionSemester, EvaluationRoomManager> evaluationRoomManagerMap =
            new HashMap<ExecutionSemester, EvaluationRoomManager>();

    public DateTime getNextDateTime(final ExecutionSemester executionPeriod) {
        EvaluationRoomManager evaluationRoomManager = evaluationRoomManagerMap.get(executionPeriod);
        if (evaluationRoomManager == null) {
            evaluationRoomManager =
                    new EvaluationRoomManager(executionPeriod.getEndDateYearMonthDay().minusDays(31).toDateTimeAtMidnight(),
                            executionPeriod.getEndDateYearMonthDay().toDateTimeAtMidnight(), 180, this);
            evaluationRoomManagerMap.put(executionPeriod, evaluationRoomManager);
        }
        return evaluationRoomManager.getNextDateTime();
    }

    public Space getNextOldRoom(final ExecutionSemester executionPeriod) {
        final EvaluationRoomManager evaluationRoomManager = evaluationRoomManagerMap.get(executionPeriod);
        return evaluationRoomManager.getNextOldRoom();
    }

}
