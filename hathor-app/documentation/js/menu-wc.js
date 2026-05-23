'use strict';

customElements.define('compodoc-menu', class extends HTMLElement {
    constructor() {
        super();
        this.isNormalMode = this.getAttribute('mode') === 'normal';
    }

    connectedCallback() {
        this.render(this.isNormalMode);
    }

    render(isNormalMode) {
        let tp = lithtml.html(`
        <nav>
            <ul class="list">
                <li class="title">
                    <a href="index.html" data-type="index-link">hathor-app documentation</a>
                </li>

                <li class="divider"></li>
                ${ isNormalMode ? `<div id="book-search-input" role="search"><input type="text" placeholder="Type to search"></div>` : '' }
                <li class="chapter">
                    <a data-type="chapter-link" href="index.html"><span class="icon ion-ios-home"></span>Getting started</a>
                    <ul class="links">
                                <li class="link">
                                    <a href="overview.html" data-type="chapter-link">
                                        <span class="icon ion-ios-keypad"></span>Overview
                                    </a>
                                </li>

                            <li class="link">
                                <a href="index.html" data-type="chapter-link">
                                    <span class="icon ion-ios-paper"></span>
                                        README
                                </a>
                            </li>
                                <li class="link">
                                    <a href="dependencies.html" data-type="chapter-link">
                                        <span class="icon ion-ios-list"></span>Dependencies
                                    </a>
                                </li>
                                <li class="link">
                                    <a href="properties.html" data-type="chapter-link">
                                        <span class="icon ion-ios-apps"></span>Properties
                                    </a>
                                </li>

                    </ul>
                </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#components-links"' :
                            'data-bs-target="#xs-components-links"' }>
                            <span class="icon ion-md-cog"></span>
                            <span>Components</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="components-links"' : 'id="xs-components-links"' }>
                            <li class="link">
                                <a href="components/AdminComponent.html" data-type="entity-link" >AdminComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AlertasComponent.html" data-type="entity-link" >AlertasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/App.html" data-type="entity-link" >App</a>
                            </li>
                            <li class="link">
                                <a href="components/AsistenteChatComponent.html" data-type="entity-link" >AsistenteChatComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AvancePasosComponent.html" data-type="entity-link" >AvancePasosComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/BenchmarkGlobalComponent.html" data-type="entity-link" >BenchmarkGlobalComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/BenchmarkingComponent.html" data-type="entity-link" >BenchmarkingComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/BenchmarkingSectionComponent.html" data-type="entity-link" >BenchmarkingSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/BenefitsSectionComponent.html" data-type="entity-link" >BenefitsSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/BitacoraOrdenioComponent.html" data-type="entity-link" >BitacoraOrdenioComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ComparativaHatosComponent.html" data-type="entity-link" >ComparativaHatosComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ComparativaKpisComponent.html" data-type="entity-link" >ComparativaKpisComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/CrearReporteComponent.html" data-type="entity-link" >CrearReporteComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/CtaSectionComponent.html" data-type="entity-link" >CtaSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/DashboardAdminComponent.html" data-type="entity-link" >DashboardAdminComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/DashboardLayoutComponent.html" data-type="entity-link" >DashboardLayoutComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/DetalleHatoAdminComponent.html" data-type="entity-link" >DetalleHatoAdminComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/DetallePracticaComponent.html" data-type="entity-link" >DetallePracticaComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/EstadisticasFinanzasComponent.html" data-type="entity-link" >EstadisticasFinanzasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/EstadisticasProduccionComponent.html" data-type="entity-link" >EstadisticasProduccionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ExcelImportSectionComponent.html" data-type="entity-link" >ExcelImportSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FeaturesSectionComponent.html" data-type="entity-link" >FeaturesSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FinanzasComponent.html" data-type="entity-link" >FinanzasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FinanzasExcelComponent.html" data-type="entity-link" >FinanzasExcelComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FinanzasManualComponent.html" data-type="entity-link" >FinanzasManualComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FooterComponent.html" data-type="entity-link" >FooterComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GestionHatosComponent.html" data-type="entity-link" >GestionHatosComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GestionPracticasComponent.html" data-type="entity-link" >GestionPracticasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GestionRecomendacionesComponent.html" data-type="entity-link" >GestionRecomendacionesComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GestionReglasComponent.html" data-type="entity-link" >GestionReglasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GraficaBarrasGlobalComponent.html" data-type="entity-link" >GraficaBarrasGlobalComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GraficaDetalleModalComponent.html" data-type="entity-link" >GraficaDetalleModalComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GraficaEvolucionPosicionComponent.html" data-type="entity-link" >GraficaEvolucionPosicionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GraficaPosicionComponent.html" data-type="entity-link" >GraficaPosicionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/GraficasBenchmarkingComponent.html" data-type="entity-link" >GraficasBenchmarkingComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HatoComponent.html" data-type="entity-link" >HatoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HeroSectionComponent.html" data-type="entity-link" >HeroSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HistorialReportesComponent.html" data-type="entity-link" >HistorialReportesComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HomeClimaComponent.html" data-type="entity-link" >HomeClimaComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HomeComponent.html" data-type="entity-link" >HomeComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HomeEstadoComponent.html" data-type="entity-link" >HomeEstadoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HomeHeaderComponent.html" data-type="entity-link" >HomeHeaderComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HomeMapaComponent.html" data-type="entity-link" >HomeMapaComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HomeModulosComponent.html" data-type="entity-link" >HomeModulosComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/HowItWorksSectionComponent.html" data-type="entity-link" >HowItWorksSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InfoBenchmarkingComponent.html" data-type="entity-link" >InfoBenchmarkingComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InfoFinanzasComponent.html" data-type="entity-link" >InfoFinanzasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InfoHatoComponent.html" data-type="entity-link" >InfoHatoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InfoProduccionComponent.html" data-type="entity-link" >InfoProduccionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InventarioGanadoComponent.html" data-type="entity-link" >InventarioGanadoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InventarioGanadoEstadisticasComponent.html" data-type="entity-link" >InventarioGanadoEstadisticasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InventarioGanadoRegistroComponent.html" data-type="entity-link" >InventarioGanadoRegistroComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InventarioGanadoResumenComponent.html" data-type="entity-link" >InventarioGanadoResumenComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InventarioGeneralComponent.html" data-type="entity-link" >InventarioGeneralComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InventarioGeneralEstadisticasComponent.html" data-type="entity-link" >InventarioGeneralEstadisticasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InventarioGeneralRegistroComponent.html" data-type="entity-link" >InventarioGeneralRegistroComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InventarioGeneralResumenComponent.html" data-type="entity-link" >InventarioGeneralResumenComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/InventariosHatoComponent.html" data-type="entity-link" >InventariosHatoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/KpiDetalleComponent.html" data-type="entity-link" >KpiDetalleComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/KpiPreviewSectionComponent.html" data-type="entity-link" >KpiPreviewSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/LandingComponent.html" data-type="entity-link" >LandingComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/LoginComponent.html" data-type="entity-link" >LoginComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/MapaHatosComponent.html" data-type="entity-link" >MapaHatosComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/MiProgresoComponent.html" data-type="entity-link" >MiProgresoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/MisPracticasComponent.html" data-type="entity-link" >MisPracticasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ModFinanzasComponent.html" data-type="entity-link" >ModFinanzasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ModGanadoComponent.html" data-type="entity-link" >ModGanadoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ModKpisComponent.html" data-type="entity-link" >ModKpisComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ModProduccionComponent.html" data-type="entity-link" >ModProduccionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/NavbarAdminComponent.html" data-type="entity-link" >NavbarAdminComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/NavbarComponent.html" data-type="entity-link" >NavbarComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/NavbarComponent-1.html" data-type="entity-link" >NavbarComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/NotFoundComponent.html" data-type="entity-link" >NotFoundComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/NuevoHatoComponent.html" data-type="entity-link" >NuevoHatoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/PracticasComponent.html" data-type="entity-link" >PracticasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/PracticasSugeridasComponent.html" data-type="entity-link" >PracticasSugeridasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/PricingSectionComponent.html" data-type="entity-link" >PricingSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ProblemSectionComponent.html" data-type="entity-link" >ProblemSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ProduccionComponent.html" data-type="entity-link" >ProduccionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ProgresoPracticasComponent.html" data-type="entity-link" >ProgresoPracticasComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ProyeccionesComponent.html" data-type="entity-link" >ProyeccionesComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/RankingComponent.html" data-type="entity-link" >RankingComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/RankingCompuestoComponent.html" data-type="entity-link" >RankingCompuestoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/RankingPorKpiComponent.html" data-type="entity-link" >RankingPorKpiComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/RankingResumenComponent.html" data-type="entity-link" >RankingResumenComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/RecomendacionesComponent.html" data-type="entity-link" >RecomendacionesComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/RegisterComponent.html" data-type="entity-link" >RegisterComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/RegistroFinancieroComponent.html" data-type="entity-link" >RegistroFinancieroComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/RegistroProduccionComponent.html" data-type="entity-link" >RegistroProduccionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ReportesComponent.html" data-type="entity-link" >ReportesComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ResetPasswordComponent.html" data-type="entity-link" >ResetPasswordComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SeccionFinancieroComponent.html" data-type="entity-link" >SeccionFinancieroComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SeccionInfoHatoComponent.html" data-type="entity-link" >SeccionInfoHatoComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SeccionInventarioComponent.html" data-type="entity-link" >SeccionInventarioComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SeccionProduccionComponent.html" data-type="entity-link" >SeccionProduccionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SidebarComponent.html" data-type="entity-link" >SidebarComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/SpinnerComponent.html" data-type="entity-link" >SpinnerComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/TestimonialsSectionComponent.html" data-type="entity-link" >TestimonialsSectionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/UsuarioComponent.html" data-type="entity-link" >UsuarioComponent</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#directives-links"' :
                                'data-bs-target="#xs-directives-links"' }>
                                <span class="icon ion-md-code-working"></span>
                                <span>Directives</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse " ${ isNormalMode ? 'id="directives-links"' : 'id="xs-directives-links"' }>
                                <li class="link">
                                    <a href="directives/RevealOnScrollDirective.html" data-type="entity-link" >RevealOnScrollDirective</a>
                                </li>
                            </ul>
                        </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#injectables-links"' :
                                'data-bs-target="#xs-injectables-links"' }>
                                <span class="icon ion-md-arrow-round-down"></span>
                                <span>Injectables</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse " ${ isNormalMode ? 'id="injectables-links"' : 'id="xs-injectables-links"' }>
                                <li class="link">
                                    <a href="injectables/AdminEstadisticasService.html" data-type="entity-link" >AdminEstadisticasService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AdminService.html" data-type="entity-link" >AdminService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AdminStateService.html" data-type="entity-link" >AdminStateService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AlertasService.html" data-type="entity-link" >AlertasService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AsistenteService.html" data-type="entity-link" >AsistenteService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AuthService.html" data-type="entity-link" >AuthService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/BenchmarkingService.html" data-type="entity-link" >BenchmarkingService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/CategoriaFinancieraService.html" data-type="entity-link" >CategoriaFinancieraService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/CategoriaGanadoService.html" data-type="entity-link" >CategoriaGanadoService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/CategoriaInventarioService.html" data-type="entity-link" >CategoriaInventarioService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/GestionPracticasService.html" data-type="entity-link" >GestionPracticasService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/GestionRecomendacionesService.html" data-type="entity-link" >GestionRecomendacionesService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/GestionReglasService.html" data-type="entity-link" >GestionReglasService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/HatoService.html" data-type="entity-link" >HatoService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/HatoStateService.html" data-type="entity-link" >HatoStateService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/InventarioGanadoService.html" data-type="entity-link" >InventarioGanadoService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/InventarioGeneralService.html" data-type="entity-link" >InventarioGeneralService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/InversionService.html" data-type="entity-link" >InversionService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/KpiService.html" data-type="entity-link" >KpiService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/NavbarStateService.html" data-type="entity-link" >NavbarStateService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/PerfilFinancieroService.html" data-type="entity-link" >PerfilFinancieroService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/PerfilProductivoService.html" data-type="entity-link" >PerfilProductivoService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/PracticasService.html" data-type="entity-link" >PracticasService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ProduccionLecheService.html" data-type="entity-link" >ProduccionLecheService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ProyeccionesService.html" data-type="entity-link" >ProyeccionesService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/RankingService.html" data-type="entity-link" >RankingService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/RazaService.html" data-type="entity-link" >RazaService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/RecomendacionesPracticasService.html" data-type="entity-link" >RecomendacionesPracticasService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/RecomendacionGeneralService.html" data-type="entity-link" >RecomendacionGeneralService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/RegistroFinancieroService.html" data-type="entity-link" >RegistroFinancieroService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ReporteService.html" data-type="entity-link" >ReporteService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/SidebarStateService.html" data-type="entity-link" >SidebarStateService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/UserService.html" data-type="entity-link" >UserService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ValorReferenciaGanadoService.html" data-type="entity-link" >ValorReferenciaGanadoService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/VentaLecheService.html" data-type="entity-link" >VentaLecheService</a>
                                </li>
                            </ul>
                        </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#guards-links"' :
                            'data-bs-target="#xs-guards-links"' }>
                            <span class="icon ion-ios-lock"></span>
                            <span>Guards</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="guards-links"' : 'id="xs-guards-links"' }>
                            <li class="link">
                                <a href="guards/AdminGuard.html" data-type="entity-link" >AdminGuard</a>
                            </li>
                            <li class="link">
                                <a href="guards/AuthGuard.html" data-type="entity-link" >AuthGuard</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#interfaces-links"' :
                            'data-bs-target="#xs-interfaces-links"' }>
                            <span class="icon ion-md-information-circle-outline"></span>
                            <span>Interfaces</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? ' id="interfaces-links"' : 'id="xs-interfaces-links"' }>
                            <li class="link">
                                <a href="interfaces/ActualizarInventarioGanadoDTO.html" data-type="entity-link" >ActualizarInventarioGanadoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ActualizarInventarioGeneralDTO.html" data-type="entity-link" >ActualizarInventarioGeneralDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ActualizarInversionDTO.html" data-type="entity-link" >ActualizarInversionDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ActualizarPerfilRapidoDTO.html" data-type="entity-link" >ActualizarPerfilRapidoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/AlertaHatoDTO.html" data-type="entity-link" >AlertaHatoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/AlertasAdminResumenDTO.html" data-type="entity-link" >AlertasAdminResumenDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/AlertasResumenDTO.html" data-type="entity-link" >AlertasResumenDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/BenchmarkGlobalDTO.html" data-type="entity-link" >BenchmarkGlobalDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/BenchmarkHatoResultado.html" data-type="entity-link" >BenchmarkHatoResultado</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/BenchmarkKpi.html" data-type="entity-link" >BenchmarkKpi</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/BenchmarkReferencia.html" data-type="entity-link" >BenchmarkReferencia</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/BenchmarkRow.html" data-type="entity-link" >BenchmarkRow</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/BenefitItem.html" data-type="entity-link" >BenefitItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CambiarEstadoDTO.html" data-type="entity-link" >CambiarEstadoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaAgrupada.html" data-type="entity-link" >CategoriaAgrupada</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaFinanciera.html" data-type="entity-link" >CategoriaFinanciera</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaGanado.html" data-type="entity-link" >CategoriaGanado</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaInventario.html" data-type="entity-link" >CategoriaInventario</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaInventario-1.html" data-type="entity-link" >CategoriaInventario</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaPersonalizada.html" data-type="entity-link" >CategoriaPersonalizada</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaPersonalizadaExcel.html" data-type="entity-link" >CategoriaPersonalizadaExcel</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriasAgrupadas.html" data-type="entity-link" >CategoriasAgrupadas</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaStat.html" data-type="entity-link" >CategoriaStat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaStat-1.html" data-type="entity-link" >CategoriaStat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CategoriaStat-2.html" data-type="entity-link" >CategoriaStat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ChatHistorialDTO.html" data-type="entity-link" >ChatHistorialDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ChatRequestDTO.html" data-type="entity-link" >ChatRequestDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ChatResponseDTO.html" data-type="entity-link" >ChatResponseDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ClimaData.html" data-type="entity-link" >ClimaData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ClimaPronosticoDia.html" data-type="entity-link" >ClimaPronosticoDia</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ComparativaHatosDTO.html" data-type="entity-link" >ComparativaHatosDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ComparativaValorStat.html" data-type="entity-link" >ComparativaValorStat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CostosFijosDTO.html" data-type="entity-link" >CostosFijosDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CrearCategoriaFinancieraDTO.html" data-type="entity-link" >CrearCategoriaFinancieraDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CrearCategoriaPersonalizadaDTO.html" data-type="entity-link" >CrearCategoriaPersonalizadaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CrearInversionDTO.html" data-type="entity-link" >CrearInversionDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CrearPracticaDTO.html" data-type="entity-link" >CrearPracticaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CrearRecomendacionAdminDTO.html" data-type="entity-link" >CrearRecomendacionAdminDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CrearRecomendacionClimaDTO.html" data-type="entity-link" >CrearRecomendacionClimaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CrearRecomendacionDTO.html" data-type="entity-link" >CrearRecomendacionDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CrearReglaDTO.html" data-type="entity-link" >CrearReglaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/CrearUsuario.html" data-type="entity-link" >CrearUsuario</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/DatosManual.html" data-type="entity-link" >DatosManual</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/DetalleCalculoItem.html" data-type="entity-link" >DetalleCalculoItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/DetallePerfilDTO.html" data-type="entity-link" >DetallePerfilDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/DistribucionGanado.html" data-type="entity-link" >DistribucionGanado</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EdadGrupoStat.html" data-type="entity-link" >EdadGrupoStat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EditarPracticaDTO.html" data-type="entity-link" >EditarPracticaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EditarReglaDTO.html" data-type="entity-link" >EditarReglaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EntradaEstadisticaDTO.html" data-type="entity-link" >EntradaEstadisticaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EstadisticasDepartamentoDTO.html" data-type="entity-link" >EstadisticasDepartamentoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EstadisticasEscalaDTO.html" data-type="entity-link" >EstadisticasEscalaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EstadisticasGlobalesDTO.html" data-type="entity-link" >EstadisticasGlobalesDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EstadisticasTropicoDTO.html" data-type="entity-link" >EstadisticasTropicoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EventoTimeline.html" data-type="entity-link" >EventoTimeline</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/EvolucionPosicionDTO.html" data-type="entity-link" >EvolucionPosicionDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FeatureItem.html" data-type="entity-link" >FeatureItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FilaEdicion.html" data-type="entity-link" >FilaEdicion</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FilaEdicion-1.html" data-type="entity-link" >FilaEdicion</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FilaResultado.html" data-type="entity-link" >FilaResultado</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FiltroAdmin.html" data-type="entity-link" >FiltroAdmin</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FiltrosAplicadosDTO.html" data-type="entity-link" >FiltrosAplicadosDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FinanzaMes.html" data-type="entity-link" >FinanzaMes</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/FraseMotivacional.html" data-type="entity-link" >FraseMotivacional</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GraficaDetalle.html" data-type="entity-link" >GraficaDetalle</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GraficaDetalle-1.html" data-type="entity-link" >GraficaDetalle</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GrupoCategoria.html" data-type="entity-link" >GrupoCategoria</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Hato.html" data-type="entity-link" >Hato</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HatoAdminDetalleDTO.html" data-type="entity-link" >HatoAdminDetalleDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HatoAdminDTO.html" data-type="entity-link" >HatoAdminDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HatoAdminSeleccionado.html" data-type="entity-link" >HatoAdminSeleccionado</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HatoAnonimizadoDTO.html" data-type="entity-link" >HatoAnonimizadoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HatoPracticaDTO.html" data-type="entity-link" >HatoPracticaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HatoPracticaPasoResponseDTO.html" data-type="entity-link" >HatoPracticaPasoResponseDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HatoRankingItem.html" data-type="entity-link" >HatoRankingItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/HatoValorDTO.html" data-type="entity-link" >HatoValorDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/InfraestructuraBasicaDTO.html" data-type="entity-link" >InfraestructuraBasicaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/InventarioGanado.html" data-type="entity-link" >InventarioGanado</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/InventarioGanadoDTO.html" data-type="entity-link" >InventarioGanadoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/InventarioGeneral.html" data-type="entity-link" >InventarioGeneral</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/InventarioGeneralDTO.html" data-type="entity-link" >InventarioGeneralDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/InversionPlaneadaDTO.html" data-type="entity-link" >InversionPlaneadaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/InversionResumenDTO.html" data-type="entity-link" >InversionResumenDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/InversionResumenDTO-1.html" data-type="entity-link" >InversionResumenDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/KpiCatalogo.html" data-type="entity-link" >KpiCatalogo</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/KpiEvolucion.html" data-type="entity-link" >KpiEvolucion</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/KpiGrupo.html" data-type="entity-link" >KpiGrupo</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/KpiGrupo-1.html" data-type="entity-link" >KpiGrupo</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/KpiHistorico.html" data-type="entity-link" >KpiHistorico</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/KpiResultado.html" data-type="entity-link" >KpiResultado</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/KpiResumenGlobalDTO.html" data-type="entity-link" >KpiResumenGlobalDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/MensajeChat.html" data-type="entity-link" >MensajeChat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/MensajeHistorialDTO.html" data-type="entity-link" >MensajeHistorialDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/MenuItem.html" data-type="entity-link" >MenuItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/MesStat.html" data-type="entity-link" >MesStat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/NavbarSection.html" data-type="entity-link" >NavbarSection</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/NavSubItem.html" data-type="entity-link" >NavSubItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PageResponse.html" data-type="entity-link" >PageResponse</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Paso.html" data-type="entity-link" >Paso</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PasoDTO.html" data-type="entity-link" >PasoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PerfilFinanciero.html" data-type="entity-link" >PerfilFinanciero</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PerfilFinancieroDetalle.html" data-type="entity-link" >PerfilFinancieroDetalle</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PerfilProductivo.html" data-type="entity-link" >PerfilProductivo</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PlanItem.html" data-type="entity-link" >PlanItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PracticaAdminDTO.html" data-type="entity-link" >PracticaAdminDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PracticaDetalleDTO.html" data-type="entity-link" >PracticaDetalleDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PracticaGeneradaDTO.html" data-type="entity-link" >PracticaGeneradaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PracticaIAResponseDTO.html" data-type="entity-link" >PracticaIAResponseDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ProblemItem.html" data-type="entity-link" >ProblemItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ProduccionLeche.html" data-type="entity-link" >ProduccionLeche</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ProyeccionesResponseDTO.html" data-type="entity-link" >ProyeccionesResponseDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ProyeccionMensualDTO.html" data-type="entity-link" >ProyeccionMensualDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PuntoEvolucion.html" data-type="entity-link" >PuntoEvolucion</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RankingCompuestoDTO.html" data-type="entity-link" >RankingCompuestoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RankingPorKpiDTO.html" data-type="entity-link" >RankingPorKpiDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RankingResumenDTO.html" data-type="entity-link" >RankingResumenDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Raza.html" data-type="entity-link" >Raza</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RazaValorStat.html" data-type="entity-link" >RazaValorStat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RecomendacionAdminDTO.html" data-type="entity-link" >RecomendacionAdminDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RecomendacionDTO.html" data-type="entity-link" >RecomendacionDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RecomendacionesResumenDTO.html" data-type="entity-link" >RecomendacionesResumenDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RecomendacionGeneralDTO.html" data-type="entity-link" >RecomendacionGeneralDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RecomendacionPracticaResumenDTO.html" data-type="entity-link" >RecomendacionPracticaResumenDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroFinanciero.html" data-type="entity-link" >RegistroFinanciero</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroFinancieroDTO.html" data-type="entity-link" >RegistroFinancieroDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroForm.html" data-type="entity-link" >RegistroForm</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroHatoDTO.html" data-type="entity-link" >RegistroHatoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroLocal.html" data-type="entity-link" >RegistroLocal</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroManualItem.html" data-type="entity-link" >RegistroManualItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroPerfilFinancieroDTO.html" data-type="entity-link" >RegistroPerfilFinancieroDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroPerfilProductivoDTO.html" data-type="entity-link" >RegistroPerfilProductivoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroProduccionLecheDTO.html" data-type="entity-link" >RegistroProduccionLecheDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RegistroVentaLecheDTO.html" data-type="entity-link" >RegistroVentaLecheDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ReglaAdminDTO.html" data-type="entity-link" >ReglaAdminDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ReglaPracticaDTO.html" data-type="entity-link" >ReglaPracticaDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ReglaResumenDTO.html" data-type="entity-link" >ReglaResumenDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ReporteConfigDTO.html" data-type="entity-link" >ReporteConfigDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ReporteHistorialDTO.html" data-type="entity-link" >ReporteHistorialDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/RespuestaVentaLeche.html" data-type="entity-link" >RespuestaVentaLeche</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ResultadoCargaMasiva.html" data-type="entity-link" >ResultadoCargaMasiva</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ResumenGanadoItem.html" data-type="entity-link" >ResumenGanadoItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ResumenInventarioItem.html" data-type="entity-link" >ResumenInventarioItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ResumenPeriodoDTO.html" data-type="entity-link" >ResumenPeriodoDTO</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ResumenPractica.html" data-type="entity-link" >ResumenPractica</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/SeccionDetalle.html" data-type="entity-link" >SeccionDetalle</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/SeccionReporte.html" data-type="entity-link" >SeccionReporte</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Stats.html" data-type="entity-link" >Stats</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/StepItem.html" data-type="entity-link" >StepItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/TestimonialItem.html" data-type="entity-link" >TestimonialItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/TipoMovimiento.html" data-type="entity-link" >TipoMovimiento</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/TipoStat.html" data-type="entity-link" >TipoStat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/TipoStat-1.html" data-type="entity-link" >TipoStat</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/TotalPorCategoria.html" data-type="entity-link" >TotalPorCategoria</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/TotalPorTipo.html" data-type="entity-link" >TotalPorTipo</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Usuario.html" data-type="entity-link" >Usuario</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/ValorReferenciaGanado.html" data-type="entity-link" >ValorReferenciaGanado</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/VentaLeche.html" data-type="entity-link" >VentaLeche</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/VincularPracticaDTO.html" data-type="entity-link" >VincularPracticaDTO</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#miscellaneous-links"'
                            : 'data-bs-target="#xs-miscellaneous-links"' }>
                            <span class="icon ion-ios-cube"></span>
                            <span>Miscellaneous</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="miscellaneous-links"' : 'id="xs-miscellaneous-links"' }>
                            <li class="link">
                                <a href="miscellaneous/typealiases.html" data-type="entity-link">Type aliases</a>
                            </li>
                            <li class="link">
                                <a href="miscellaneous/variables.html" data-type="entity-link">Variables</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <a data-type="chapter-link" href="routes.html"><span class="icon ion-ios-git-branch"></span>Routes</a>
                        </li>
                    <li class="chapter">
                        <a data-type="chapter-link" href="coverage.html"><span class="icon ion-ios-stats"></span>Documentation coverage</a>
                    </li>
                    <li class="divider"></li>
                    <li class="copyright">
                        Documentation generated using <a href="https://compodoc.app/" target="_blank" rel="noopener noreferrer">
                            <img data-src="images/compodoc-vectorise.png" class="img-responsive" data-type="compodoc-logo">
                        </a>
                    </li>
            </ul>
        </nav>
        `);
        this.innerHTML = tp.strings;
    }
});