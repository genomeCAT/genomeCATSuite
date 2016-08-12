package org.molgen.genomeCATPro.datadb.dbentities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @name AnnotationList
 *
 *
 * @author Katrin Tebel <tebel at molgen.mpg.de>
 * This file is part of the CGHPRO software package. Copyright Aug 24, 2010
 * Katrin Tebel <tebel at molgen.mpg.de>. The contents of this file are subject
 * to the terms of either the GNU General Public License Version 2 only ("GPL")
 * or the Common Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the License.
 * You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and
 * limitations under the License. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
@Entity
@Table(name = "AnnotationList")
public class AnnotationList implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "AnnotationID", nullable = false)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "genomeRelease", nullable = false)
    private String genomeRelease;
    @Lob
    @Column(name = "description")
    private String description;
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "tableData", nullable = false)
    private String tableData;
    @Column(name = "color", nullable = false)
    private Long color;
    @Column(name = "transparency", nullable = false)
    private Double transparency;
    @Column(name = "clazz", nullable = false)
    private String clazz;

    public AnnotationList() {
    }

    public AnnotationList(Long id) {
        this.id = id;
    }

    public AnnotationList(AnnotationList a) {
        this.id = a.id;
        this.genomeRelease = a.genomeRelease;
        this.name = a.name;
        this.description = a.description;
        this.clazz = a.clazz;

        this.modified = a.modified;
        this.created = a.created;
        this.tableData = a.tableData;

        this.color = a.color;
        this.transparency = a.transparency;
    }

    public AnnotationList(Long id, String genomeRelease, String genome, Date modified, Date created, String tableData) {
        this.id = id;
        this.genomeRelease = genomeRelease;

        this.modified = modified;
        this.created = created;
        this.tableData = tableData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenomeRelease() {
        return genomeRelease;
    }

    public void setGenomeRelease(String genomeRelease) {
        this.genomeRelease = genomeRelease;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTableData() {
        return tableData;
    }

    public void setTableData(String tableData) {
        this.tableData = tableData;
    }

    public Long getColor() {
        return this.color;
    }

    public void setColor(Long color) {
        this.color = color;
    }

    public Double getTransparency() {
        return transparency;
    }

    public void setTransparency(Double transparency) {
        this.transparency = transparency;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AnnotationList)) {
            return false;
        }
        AnnotationList other = (AnnotationList) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.AnnotationList[id=" + id + "] "
                + getName() + "," + this.getDescription() + ", ";
    }

    public void persist(Object object) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("firstDraftPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}
