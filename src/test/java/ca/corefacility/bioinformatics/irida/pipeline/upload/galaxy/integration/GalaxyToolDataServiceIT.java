package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyToolDataTableException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.ToolDataClient;
import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.sun.jersey.api.client.ClientHandlerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for accessing Galaxy Tool Data Tables.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class })

public class GalaxyToolDataServiceIT {

    @Autowired
    private LocalGalaxy localGalaxy;
    private GalaxyInstance galaxyInstanceAdmin;
    private ToolDataClient toolDataClient;
    private GalaxyToolDataService galaxyToolDataService;

    private static final String INVALID_TOOL_DATA_TABLE_ID = "";
    private static final String VALID_TOOL_DATA_TABLE_ID = "igv_broad_genomes";
    private static final String VALID_TOOL_DATA_VALUE = "hg38";
    private static final String VALID_TOOL_DATA_COLUMN = "url";
    private static final List<String> VALID_TOOL_DATA_COLUMN_FIELDS = new ArrayList<>(
            Arrays.asList(
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ABaumannii_ATCC_17978.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ABaumannii_AYE_uid61637.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/AgamP3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/anidulans_4.1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tair10.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tair8.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tair9.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/Aplysia.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_001623.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_002929.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_000964.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/MusaBalbisianaPKWv1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/MusaAcuminata.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/candida.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ca21.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/WS201.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/WS220.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ws241.genome",
                    "https://s3.amazonaws.com/igv.broadinstitute.org/genomes/ws235.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/WS245.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ce10.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ce11.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ce4.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ce6.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/CSavignyi_v2.1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_009012.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/galGal3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/galGal4.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/galGal5.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/panTro2.genome",
                    "https://s3.amazonaws.com/igv.broadinstitute.org/genomes/panTro3.genome",
                    "https://s3.amazonaws.com/igv.broadinstitute.org/genomes/panTro4.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/felCat5.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/bosTau3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/bosTau4.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/bosTau6.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/bosTau7.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/bosTau8.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/D.discoideum.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/dmel_5.9.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/dm2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/dm3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/dm6.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/dmel_r5.22.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/dmel_r5.33.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/canFam2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/canFam3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_000913.2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/U00096.2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_000913.3.gbk",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_002655.2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_001422.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/MusPutFur1.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_008601.gbk",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/Foxy4287.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/Glamblia_2.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/gmax8.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/gmax10.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/cavPor3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_004917.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_001802.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_001722.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/equCab2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/1kg_ref.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/1kg_v37.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/b37.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_001405.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/HHV4_Type1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/HHV4_Type2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/hg16.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/hg17.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/hg18.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/hg19.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/hg38.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_012920.1.gbk",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/M74568.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/lmjr.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/lmjr_4.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/mg8.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/CE_1.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/B73.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ZmB73_5a.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/SvImJ.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/mm10.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/mm7.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/mm8.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/mm9.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_002755.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/test/genomes/NC_008767.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/test/genomes/NC_003112.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/test/genomes/NC_003116.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/nc10.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ncrassa_v3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/O_Sativa_r6.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/osativa_6.1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/osativa_7.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/monDom5.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/Pf3D7_v9.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ppatens_1.2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/pvivax.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/PlasmoDB_7.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/PlasmoDB_8.2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/Plasmodium_3D7_v2.1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/Plasmodium_3D7_v5.5.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/Plasmodium_6.1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/PlasmoDB_24.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/oryCun2.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/rn4.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/rn5.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/rn6.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/rheMac2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/rheMac3.genome",
                    "https://s3.amazonaws.com/igv.broadinstitute.org/genomes/rheMac8.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/sacCer62.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/GSM552910.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/Y55.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/sacCer1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/sacCer2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/sacCer3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/sk1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/spombe_709.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/spombe_1.55.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/spur_2.5.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/spur_3.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/sclerotiorum.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/spur_2.1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/NC_016856.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/GCF_000233375.1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/oviAri3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/gasAcu1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/susScrofa.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/susScr3.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tb427_4.2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tb927.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tbrucei927_4.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tbrucei927_5.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tbgambi.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tbgambi_4.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tcas_2.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/tcas_3.0.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/T_gondii_me49.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/SL2.31.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/SL2.40.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/ITAG2.3.genome",
                    "https://s3.amazonaws.com/igv.broadinstitute.org/genomes/CCAF000000000.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/vvinifera.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/VcholeraeN16961.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/xenTro2.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/xenTro9.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/laevis_7.1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/taeGut1.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/zebrafish.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/danRer6.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/danRer7.genome",
                    "http://s3.amazonaws.com/igv.broadinstitute.org/genomes/danRer10.genome",
                    "https://s3.amazonaws.com/igv.broadinstitute.org/genomes/AGPv3.31.genome"
            )
    );


    /**
     * Sets up for tool data table tests.
     */
    @Before
    public void setup() {
        galaxyInstanceAdmin = localGalaxy.getGalaxyInstanceAdmin();
        ToolDataClient toolDataClient = galaxyInstanceAdmin.getToolDataClient();
        galaxyToolDataService = new GalaxyToolDataService(toolDataClient);

    }

    /**
     * Tests getting a valid workflow input id from a workflow details.
     * @throws GalaxyDatasetNotFoundException
     */
    @Test
    public void testGetToolDataTableValid() throws GalaxyDatasetNotFoundException {
        TabularToolDataTable toolDataTable;
        try {
            toolDataTable = galaxyToolDataService.getToolDataTable(VALID_TOOL_DATA_TABLE_ID);
        } catch (Exception e) {
            throw new GalaxyDatasetNotFoundException(e);
        }
        assertNotNull(toolDataTable);
        assertEquals(VALID_TOOL_DATA_TABLE_ID, toolDataTable.getName());
    }

    /**
     * Tests getting a valid workflow input id from a workflow details.
     * @throws GalaxyDatasetNotFoundException
     */
    @Test
    public void testGetToolDataFieldValid() throws GalaxyToolDataTableException {
        TabularToolDataTable toolDataTable;
        try {
            toolDataTable = galaxyToolDataService.getToolDataTable(VALID_TOOL_DATA_TABLE_ID);
        } catch (Exception e) {
            throw new GalaxyToolDataTableException(e);
        }
        assertEquals("http://s3.amazonaws.com/igv.broadinstitute.org/genomes/hg38.genome",
                galaxyToolDataService.getToolDataField(toolDataTable, VALID_TOOL_DATA_VALUE, VALID_TOOL_DATA_COLUMN));
    }

    /**
     * Tests getting a valid workflow input id from a workflow details.
     * @throws GalaxyToolDataTableException
     */
    @Test
    public void testGetToolDataColumnValid() throws GalaxyToolDataTableException {
        TabularToolDataTable toolDataTable;
        try {
            toolDataTable = galaxyToolDataService.getToolDataTable(VALID_TOOL_DATA_TABLE_ID);
        } catch (Exception e) {
            throw new GalaxyToolDataTableException(e);
        }
        assertEquals(VALID_TOOL_DATA_COLUMN_FIELDS,
                galaxyToolDataService.getToolDataColumn(toolDataTable, VALID_TOOL_DATA_COLUMN));
    }

    /**
     * Tests failing to find a valid workflow input id from a workflow details.
     * @throws GalaxyDatasetNotFoundException
     */
    @Test(expected=GalaxyDatasetNotFoundException.class)
    public void testGetToolDataInvalid() throws GalaxyToolDataTableException {
        TabularToolDataTable toolDataTable;
        try {
            toolDataTable = galaxyToolDataService.getToolDataTable(INVALID_TOOL_DATA_TABLE_ID);
        } catch (ClientHandlerException e) {
            throw new GalaxyToolDataTableException(e);
        }
    }
}
