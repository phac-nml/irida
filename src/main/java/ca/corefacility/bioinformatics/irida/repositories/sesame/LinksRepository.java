/*
 * Copyright 2013 Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.corefacility.bioinformatics.irida.repositories.sesame;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Link;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.alibaba.LinkIF;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.StringIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class LinksRepository extends SesameRepository{
    
    public final String linkType = "http://corefacility.ca/irida/ResourceLink";
    AuditRepository auditRepo;
    
    public LinksRepository(TripleStore store,AuditRepository auditRepo) {
        super(store,"ResourceLink");
        this.auditRepo = auditRepo;
    }
    
    public StringIdentifier buildIdentifier(BindingSet bs){
        StringIdentifier id = null;
        try {
            Value uri = bs.getValue("object");
            Value ident = bs.getValue("identifier");
            Value label = bs.getValue("label");
            id = new StringIdentifier();
            id.setIdentifier(ident.stringValue());
            id.setUri(new java.net.URI(uri.stringValue()));
            id.setLabel(label.stringValue());
        } catch (URISyntaxException ex) {
            Logger.getLogger(LinksRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return id;
    }
    
    public Link create(Link link){
        Identifier subject = link.getSubject();
        Identifier object = link.getObject();
        RdfPredicate predicate = link.getRelationship();
        
        java.net.URI subNetURI = getUriFromIdentifier(subject);
        java.net.URI objNetURI = getUriFromIdentifier(object);
        
        ObjectConnection con = store.getRepoConnection();
        ValueFactory fac = con.getValueFactory();
        
        try {
            con.begin();
            URI subURI = fac.createURI(subNetURI.toString());
            URI objURI = fac.createURI(objNetURI.toString());            
            URI pred = fac.createURI(con.getNamespace(predicate.prefix), predicate.name);
            
            Identifier identifier = generateIdentifier();
            java.net.URI netURI = identifier.getUri();
            URI linkURI = fac.createURI(netURI.toString());
            
            Statement st = fac.createStatement(subURI, pred, objURI);
            con.add(st);
            
            URI rdftype = fac.createURI(con.getNamespace("rdf"), "type");
            URI type = fac.createURI(linkType);
            
            URI linkSubject = fac.createURI(con.getNamespace("irida"), "linkSubject");
            URI linkPredicate = fac.createURI(con.getNamespace("irida"), "linkPredicate");
            URI linkObject = fac.createURI(con.getNamespace("irida"), "linkObject");
            
            con.add(fac.createStatement(linkURI, rdftype, type));
            con.add(fac.createStatement(linkURI, linkSubject, subURI));
            con.add(fac.createStatement(linkURI, linkPredicate, pred));
            con.add(fac.createStatement(linkURI, linkObject, objURI));

            auditRepo.audit(link.getAuditInformation(), linkURI.toString());
            
            con.commit();
            
        } catch (RepositoryException ex) {
            Logger.getLogger(LinksRepository.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally{
            store.closeRepoConnection(con);
        }
        
        return link;
    }
    
   
    public List<Identifier> listObjects(Identifier subjectId, RdfPredicate predicate){
        
        List<Identifier> ids = new ArrayList<>();
        java.net.URI subNetURI = getUriFromIdentifier(subjectId);
        
        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?obj ?id "
                    + "WHERE{ ?sub ?pred ?object .\n"
                    + "?obj irida:identifier ?identifier ;"
                    + " rdfs:label ?label .\n"
                    + "}";
            ValueFactory fac = con.getValueFactory();
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            URI subURI = fac.createURI(subNetURI.toString());
            //URI predURI = fac.createURI(predicate);
            URI predURI = fac.createURI(con.getNamespace(predicate.prefix), predicate.name);
            query.setBinding("sub", subURI);
            query.setBinding("pred", predURI);
            TupleQueryResult results = query.evaluate();
            while(results.hasNext()){
                BindingSet bs = results.next();
                ids.add(buildIdentifier(bs));
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(LinksRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;
    }
            
    //@Override
    public Link buildObject(LinkIF base, Identifier i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
       
}
