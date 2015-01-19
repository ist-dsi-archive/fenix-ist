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
package pt.ist.fenix.ui.struts.action.research.researchUnit;

import java.io.InputStream;
import java.io.Serializable;

import org.fenixedu.academic.domain.organizationalStructure.Unit;

public class UnitFileUploadBean extends UnitFileBean implements Serializable {

    private final Unit unit;

    private String fileName;
    private Long fileSize;

    transient private InputStream uploadFile;
    private String authorsName;

    public UnitFileUploadBean(Unit unit) {
        super();
        this.unit = unit;
    }

    @Override
    public Unit getUnit() {
        return this.unit;
    }

    public String getAuthorsName() {
        return authorsName;
    }

    public void setAuthorsName(String authorsName) {
        this.authorsName = authorsName;
    }

    public InputStream getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(InputStream file) {
        this.uploadFile = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
