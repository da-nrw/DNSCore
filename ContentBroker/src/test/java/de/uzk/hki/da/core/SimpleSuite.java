/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.uzk.hki.da.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uzk.hki.da.cb.AbstractActionTests;
import de.uzk.hki.da.cb.ArchiveReplicationActionTests;
import de.uzk.hki.da.cb.BuildAIPActionTests;
import de.uzk.hki.da.cb.CheckFormatsActionTest;
import de.uzk.hki.da.cb.ConversionInstructionsBuilderTests;
import de.uzk.hki.da.cb.ConvertActionNodeSplittingTests;
import de.uzk.hki.da.cb.ConvertActionTests;
import de.uzk.hki.da.cb.ConvertCheckActionTests;
import de.uzk.hki.da.cb.CreateEDMActionTests;
import de.uzk.hki.da.cb.CreatePremisActionTests;
import de.uzk.hki.da.cb.DeleteObjectActionTests;
import de.uzk.hki.da.cb.FetchPIPsActionTest;
import de.uzk.hki.da.cb.IndexMetadataActionTests;
import de.uzk.hki.da.cb.ObjectTest;
import de.uzk.hki.da.cb.PrepareSendToPresenterActionTests;
import de.uzk.hki.da.cb.ProperRefreshOfJobChildrenTest;
import de.uzk.hki.da.cb.RestructureActionTests;
import de.uzk.hki.da.cb.RetrievalActionTest;
import de.uzk.hki.da.cb.ScanActionTests;
import de.uzk.hki.da.cb.SelectProcessingNodesTests;
import de.uzk.hki.da.cb.SendToPresenterActionTests;
import de.uzk.hki.da.cb.ShortenFileNamesActionTests;
import de.uzk.hki.da.cb.TarActionTests;
import de.uzk.hki.da.cb.UnpackActionTests;
import de.uzk.hki.da.cb.UpdateMetadataActionEADMultilevelPackagesTest;
import de.uzk.hki.da.cb.UpdateMetadataActionEADTests;
import de.uzk.hki.da.cb.UpdateMetadataActionLIDOTests;
import de.uzk.hki.da.cb.UpdateMetadataActionTests;
import de.uzk.hki.da.cb.UpdateMetadataActionXMPTests;
import de.uzk.hki.da.cb.ValidateMetadataActionTests;
import de.uzk.hki.da.format.CLIConversionStrategyTests;
import de.uzk.hki.da.format.ConverterServiceTests;
import de.uzk.hki.da.format.FidoCLITest;
import de.uzk.hki.da.format.FormatScanServiceTests;
import de.uzk.hki.da.format.PDFServiceTests;
import de.uzk.hki.da.format.PdfConversionStrategyTest;
import de.uzk.hki.da.format.PublishAudioConversionStrategyTests;
import de.uzk.hki.da.format.PublishImageConversionStrategyTest;
import de.uzk.hki.da.format.PublishImageMultipageTIFFTests;
import de.uzk.hki.da.format.PublishPDFConversionStrategyTests;
import de.uzk.hki.da.format.PublishXsltConversionStrategyTests;
import de.uzk.hki.da.format.TiffConversionStrategyTests;
import de.uzk.hki.da.grid.IrodsGridFacadeTest;
import de.uzk.hki.da.grid.ReplicationExecutorTest;
import de.uzk.hki.da.integrity.IntegrityScannerWorkerTest;
import de.uzk.hki.da.metadata.MetsRightsSectionXmlReaderTest;
import de.uzk.hki.da.metadata.MetsURNXmlReaderTest;
import de.uzk.hki.da.metadata.PremisXmlReaderTests;
import de.uzk.hki.da.metadata.PremisXmlWriterTest;
import de.uzk.hki.da.metadata.XsltEDMGeneratorTests;
import de.uzk.hki.da.model.CentralDatabaseDAOTests;
import de.uzk.hki.da.model.GetNewestFilesFromAllRepresentationsTests;
import de.uzk.hki.da.model.JobCascadingTest;
import de.uzk.hki.da.model.ModelTest;
import de.uzk.hki.da.model.PackageTests;
import de.uzk.hki.da.model.PreservationSystemTests;
import de.uzk.hki.da.model.SaveConversionInstructionsByJob;
import de.uzk.hki.da.service.RetrievePackagesHelperTest;
import de.uzk.hki.da.service.URNCheckDigitGeneratorTests;
import de.uzk.hki.da.service.XPathUtilsTests;
import de.uzk.hki.da.utils.ArchiveBuilderTests;
import de.uzk.hki.da.utils.CommaSeparatedListTests;
import de.uzk.hki.da.utils.PathTest;
import de.uzk.hki.da.utils.PathTypeEditorSupportTests;

/**
 * The collection of all our unit tests which should run pre and post commit.
 */
@RunWith(Suite.class)
@SuiteClasses({
	
	PrepareSendToPresenterActionTests.class,
	IngestAreaScannerWorkerTests.class,
	ProperRefreshOfJobChildrenTest.class,
	JobCascadingTest.class,
	ConvertActionTests.class,
	SaveConversionInstructionsByJob.class,
	CreatePremisActionTests.class,
	UnpackActionTests.class,
	CLIConversionStrategyTests.class,
	ObjectTest.class,
	ConversionInstructionsBuilderTests.class,
	CentralDatabaseDAOTests.class,
	SelectProcessingNodesTests.class,
	ActionFactoryTests.class,
	RetrievePackagesHelperTest.class,
	ActionRegistryTests.class,
	TarActionTests.class,
	BuildAIPActionTests.class,
	ModelTest.class,
	ConvertCheckActionTests.class,
	ConvertActionNodeSplittingTests.class,
	ConverterServiceTests.class,
	ScanActionTests.class,
	ArchiveBuilderTests.class,
	FormatScanServiceTests.class,
	FetchPIPsActionTest.class,
	PreservationSystemTests.class,
	GetNewestFilesFromAllRepresentationsTests.class,
	RetrievalActionTest.class,
	PremisXmlWriterTest.class,
	IrodsGridFacadeTest.class,
	PublishImageConversionStrategyTest.class,
//	PublishVideoConversionStrategyTests.class, XXX should not be dependent on handbrake installation
	PublishXsltConversionStrategyTests.class,
	PublishAudioConversionStrategyTests.class,
	PublishImageMultipageTIFFTests.class,
	UpdateMetadataActionTests.class,
	PremisXmlReaderTests.class,
	MetsRightsSectionXmlReaderTest.class,
	MetsURNXmlReaderTest.class,
	IntegrityScannerWorkerTest.class,
	ShortenFileNamesActionTests.class,
	XPathUtilsTests.class,
	CommaSeparatedListTests.class,
	CheckFormatsActionTest.class,
	PdfConversionStrategyTest.class,
	PublishPDFConversionStrategyTests.class,
	TiffConversionStrategyTests.class,
	URNCheckDigitGeneratorTests.class,
	RegisterObjectServiceTests.class,
	PDFServiceTests.class,
	PackageTests.class,
	ArchiveReplicationActionTests.class,
	FidoCLITest.class,
	UpdateMetadataActionEADTests.class,
	DeleteObjectActionTests.class,
	UpdateMetadataActionXMPTests.class,
	PathTest.class,
	AbstractActionTests.class,
	ReplicationExecutorTest.class,
	PathTypeEditorSupportTests.class,
	XsltEDMGeneratorTests.class,
	SendToPresenterActionTests.class,
	CreateEDMActionTests.class,
	IndexMetadataActionTests.class,
	UpdateMetadataActionEADMultilevelPackagesTest.class,
	UpdateMetadataActionLIDOTests.class,
	IndexMetadataActionTests.class,
	RestructureActionTests.class,
	ValidateMetadataActionTests.class
})
public class SimpleSuite {

}
