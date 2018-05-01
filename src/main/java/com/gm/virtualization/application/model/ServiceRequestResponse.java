package com.gm.virtualization.application.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gm.virtualization.util.UrlUtil;
import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheIndex;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;
import static org.eclipse.persistence.annotations.CacheType.FULL;
import static org.eclipse.persistence.config.QueryHints.*;
import static org.eclipse.persistence.config.QueryType.Auto;

/**
 * JPA entities encapsulating service request/response
 */
@Getter
@Setter()
@ToString(exclude = {"request", "response"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SERVICE_RESPONSE")
@Cache(type = FULL, // Cache which will be never removed even GC
        size = 100000 //as the initial cache size
)
@CacheIndex(columnNames = {"URL", "METHOD", "REQ_HASH_KEY"})
@NamedQueries(
        {
                @NamedQuery(name = "ServiceRequestResponse.lookUpRequestResponse", query = " select o from ServiceRequestResponse o where o.url = ?1 and  o.httpMethod = ?2 and o.requestHashKey = ?3",
                        hints = {

                                @QueryHint(name = QUERY_RESULTS_CACHE, value = "true"),
                                @QueryHint(name = QUERY_RESULTS_CACHE_SIZE, value = "100000"),
                                @QueryHint(name = QUERY_RESULTS_CACHE_IGNORE_NULL, value = "true"),
                                @QueryHint(name = QUERY_RESULTS_CACHE_TYPE, value = "FULL"),
                                @QueryHint(name = QUERY_TYPE, value = Auto)

                        }
                ),
                @NamedQuery(name = "ServiceRequestResponse.lookUpGroovyRequestResponse", query = " select o from ServiceRequestResponse o where o.url = ?1 and  o.httpMethod = ?2 and length(o.groovyHashKey) > 0",
                        hints = {

                                @QueryHint(name = QUERY_RESULTS_CACHE, value = "true"),
                                @QueryHint(name = QUERY_RESULTS_CACHE_SIZE, value = "100000"),
                                @QueryHint(name = QUERY_RESULTS_CACHE_IGNORE_NULL, value = "true"),
                                @QueryHint(name = QUERY_RESULTS_CACHE_TYPE, value = "FULL"),
                                @QueryHint(name = QUERY_TYPE, value = Auto)

                        }
                ),
                @NamedQuery(name = "ServiceRequestResponse.lookUpTcpRequestResponse", query = " select o from ServiceRequestResponse o where o.url = ?1 and o.requestHashKey = ?2 and o.protcol = 'tcp'",
                        hints = {

                                @QueryHint(name = QUERY_RESULTS_CACHE, value = "true"),
                                @QueryHint(name = QUERY_RESULTS_CACHE_SIZE, value = "100000"),
                                @QueryHint(name = QUERY_RESULTS_CACHE_IGNORE_NULL, value = "true"),
                                @QueryHint(name = QUERY_RESULTS_CACHE_TYPE, value = "FULL"),
                                @QueryHint(name = QUERY_TYPE, value = Auto)

                        }
                )


        }
)

public class ServiceRequestResponse {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = AUTO)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Long id;

    @Basic
    @Column(name = "PROTOCOL")
    @JsonIgnore
    private String protcol = "http";


    @Index
    @Basic
    @Column(name = "URL")
    @Expose
    private String url;

    @Basic
    @Column(name = "METHOD")
    @Expose
    private String httpMethod;


    @Basic
    @Column(name = "STATUS")
    @Expose
    private String status;

    @Basic
    @Column(name = "HEADER")
    @Expose
    @Lob
    private String httpHeaders;

    @Basic
    @Column(name = "REQUEST")
    @Lob
    @Expose
    private String request;

    @Basic
    @Column(name = "GROOVY_TEMPLATE")
    @Lob
    @Expose
    private String groovyTemplate;

    @Basic
    @Column(name = "RESPONSE")
    @Lob
    @Expose
    private String response;

    @Basic
    @Column(name = "REQ_HASH_KEY")
    @Index
    @Expose
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String requestHashKey;


    @Basic
    @Column(name = "Groovy_HASH_KEY")
    @Index
    @Expose
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String groovyHashKey;


    public void setUrl(String url) {

        this.url = UrlUtil.getCorrectUrl(url);
    }

    public void doCorrectUrl() {
        setUrl(this.url);
    }
}
