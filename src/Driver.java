import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Driver{
    static final int NUMBER_OF_ANTS=500;
    static final double PROCESSING_CYCLE_PROBABILITY=0.8;
    static final double PHEROMONE_EVAP_FACTOR=0.9;

    static ArrayList<Taxa> initialRoute=new ArrayList<Taxa>(Arrays.asList(
            new Taxa("BlueWhale","Balaenoptera musculus",0), //======20-Species with online distances=======//
            new Taxa("Cat","Felis catus",1),
            new Taxa("Chimpanzee","Pan troglodytes",2),
            new Taxa("Cow","Bos taurus",3),
            new Taxa("FinbackWhale","Balaenoptera physalus",4),
            new Taxa("Gibbon","Hylobates lar",5),
            new Taxa("Gorilla","Gorilla gorilla",6),
            new Taxa("GreySeal","Halichoerus grypus",7),
            new Taxa("HarborSeal","Phoca vitulina",8),
            new Taxa("Horse ","Equus caballus",9),
            new Taxa("HouseMouse","Mus musculus",10),
            new Taxa("Human","Homo sapiens",11),
            new Taxa("Opossum","Didelphis virginiana",12),
            new Taxa("Orangutan","Pongo pygmaeus",13)
            /*new Taxa("Platypus","Ornithorhynchus anatinus",14),
            new Taxa("PygmyChimpanzee","Pan paniscus",15),
            new Taxa("Rat","Rattus norvegicus",16),
            new Taxa("SumatranOrangutan","Pongo abeli",17),
            new Taxa("Wallaroo","Macropus robustus",18),
            new Taxa("WhiteRhino","Rhinoceros unicornis",19)*/
    ));

    static double[][] basicDistancesMatrix=new double[][]{
            /*Distances for 20-species from ONLINE data source */
            //  0           1           2       3           4       5       6           7       8           9       10          11      12          13      14          15      16          17          18      19
           /* {0.000000, 0.956323, 0.972438, 0.933378, 0.602634, 0.976144, 0.979629, 0.952030, 0.951910, 0.944889, 0.969844, 0.976034, 0.981995, 0.977991, 0.982811, 0.974382, 0.972313, 0.976209, 0.982206, 0.945881},
            {0.956323, 0.000000, 0.974313, 0.944650, 0.956363, 0.975953, 0.975125, 0.926396, 0.928524, 0.932140, 0.964397, 0.977328, 0.979132, 0.977075, 0.982982, 0.974569, 0.964113, 0.979347, 0.976386, 0.937550},
            {0.972438, 0.974313, 0.000000, 0.970806, 0.977246, 0.873617, 0.727796, 0.972297, 0.971715, 0.968993, 0.981926, 0.657387, 0.985329, 0.851432, 0.987256, 0.428991, 0.976425, 0.850383, 0.986032, 0.969842},
            {0.933378, 0.944650, 0.970806, 0.000000, 0.938797, 0.972798, 0.972197, 0.941317, 0.942067, 0.940301, 0.967403, 0.973620, 0.978456, 0.973127, 0.980380, 0.970828, 0.969176, 0.977041, 0.973974, 0.933010},
            {0.602634, 0.956363, 0.977246, 0.938797, 0.000000, 0.981417, 0.979665, 0.957940, 0.958546, 0.947599, 0.976451, 0.980493, 0.981616, 0.978559, 0.982824, 0.979584, 0.970869, 0.980316, 0.982494, 0.948306},
            {0.976144, 0.975953, 0.873617, 0.972798, 0.981417, 0.000000, 0.880709, 0.973661, 0.976154, 0.969450, 0.981696, 0.880203, 0.984314, 0.891729, 0.990322, 0.875655, 0.979186, 0.891283, 0.985513, 0.968633},
            {0.979629, 0.975125, 0.727796, 0.972197, 0.979665, 0.880709, 0.000000, 0.975026, 0.974955, 0.973320, 0.982952, 0.732325, 0.986312, 0.857432, 0.986569, 0.720617, 0.982154, 0.854869, 0.986469, 0.969977},
            {0.952030, 0.926396, 0.972297, 0.941317, 0.957940, 0.973661, 0.975026, 0.000000, 0.428044, 0.938868, 0.968053, 0.975760, 0.979947, 0.978015, 0.981756, 0.975585, 0.967721, 0.976995, 0.977296, 0.933762},
            {0.951910, 0.928524, 0.971715, 0.942067, 0.958546, 0.976154, 0.974955, 0.428044, 0.000000, 0.934823, 0.965112, 0.974737, 0.980340, 0.978275, 0.981906, 0.974849, 0.966301, 0.975989, 0.977183, 0.934832},
            {0.944889, 0.932140, 0.968993, 0.940301, 0.947599, 0.969450, 0.973320, 0.938868, 0.934823, 0.000000, 0.972751, 0.971558, 0.977220, 0.975414, 0.984268, 0.970180, 0.964328, 0.975455, 0.978211, 0.879802},
            {0.969844, 0.964397, 0.981926, 0.967403, 0.976451, 0.981696, 0.982952, 0.968053, 0.965112, 0.972751, 0.000000, 0.980400, 0.974406, 0.983850, 0.981355, 0.980623, 0.901139, 0.983575, 0.973949, 0.968500},
            {0.976034, 0.977328, 0.657387, 0.973620, 0.980493, 0.880203, 0.732325, 0.975760, 0.974737, 0.971558, 0.980400, 0.000000, 0.986243, 0.847139, 0.988041, 0.654234, 0.981715, 0.841775, 0.985926, 0.973694},
            {0.981995, 0.979132, 0.985329, 0.978456, 0.981616, 0.984314, 0.986312, 0.979947, 0.980340, 0.977220, 0.974406, 0.986243, 0.000000, 0.987712, 0.984083, 0.985832, 0.979062, 0.986586, 0.942194, 0.979566},
            {0.977991, 0.977075, 0.851432, 0.973127, 0.978559, 0.891729, 0.857432, 0.978015, 0.978275, 0.975414, 0.983850, 0.847139, 0.987712, 0.000000, 0.990018, 0.849235, 0.982004, 0.569297, 0.986478, 0.978454},
            {0.982811, 0.982982, 0.987256, 0.980380, 0.982824, 0.990322, 0.986569, 0.981756, 0.981906, 0.984268, 0.981355, 0.988041, 0.984083, 0.990018, 0.000000, 0.987128, 0.983857, 0.989407, 0.981747, 0.986069},
            {0.974382, 0.974569, 0.428991, 0.970828, 0.979584, 0.875655, 0.720617, 0.975585, 0.974849, 0.970180, 0.980623, 0.654234, 0.985832, 0.849235, 0.987128, 0.000000, 0.976925, 0.849978, 0.984617, 0.971307},
            {0.972313, 0.964113, 0.976425, 0.969176, 0.970869, 0.979186, 0.982154, 0.967721, 0.966301, 0.964328, 0.901139, 0.981715, 0.979062, 0.982004, 0.983857, 0.976925, 0.000000, 0.979808, 0.976307, 0.964887},
            {0.976209, 0.979347, 0.850383, 0.977041, 0.980316, 0.891283, 0.854869, 0.976995, 0.975989, 0.975455, 0.983575, 0.841775, 0.986586, 0.569297, 0.989407, 0.849978, 0.979808, 0.000000, 0.988176, 0.975742},
            {0.982206, 0.976386, 0.986032, 0.973974, 0.982494, 0.985513, 0.986469, 0.977296, 0.977183, 0.978211, 0.973949, 0.985926, 0.942194, 0.986478, 0.981747, 0.984617, 0.976307, 0.988176, 0.000000, 0.972083},
            {0.945881, 0.937550, 0.969842, 0.933010, 0.948306, 0.968633, 0.969977, 0.933762, 0.934832, 0.879802, 0.968500, 0.973694, 0.979566, 0.978454, 0.986069, 0.971307, 0.964887, 0.975742, 0.972083, 0.000000}
*/
            /*{0.000000, 0.956323, 0.972438, 0.933378, 0.602634 },
            { 0.956323, 0.000000, 0.974313, 0.944650, 0.956363},
            {0.972438, 0.974313, 0.000000, 0.970806, 0.977246 },
            { 0.933378, 0.944650, 0.970806, 0.000000, 0.938797 },
            { 0.602634, 0.956363, 0.977246, 0.938797, 0.000000 }*/

            /*----------------5-Species--------------*/
       //         0           1         2         3         4         5
            /*{0.000000, 0.956323, 0.972438, 0.933378, 0.602634, 0.976144},
            { 0.956323, 0.000000, 0.974313, 0.944650, 0.956363, 0.975953},
            {0.972438, 0.974313, 0.000000, 0.970806, 0.977246, 0.873617},
            { 0.933378, 0.944650, 0.970806, 0.000000, 0.938797, 0.972798},
            { 0.602634, 0.956363, 0.977246, 0.938797, 0.000000, 0.981417 },
            { 0.976144, 0.975953, 0.873617, 0.972798, 0.981417, 0.000000}*/

            /*----------------10-Species--------------*/
            //   0           1         2       3         4         5        6           7         8         9
          /*  {0.000000, 0.956323, 0.972438, 0.933378, 0.602634, 0.976144, 0.979629, 0.952030, 0.951910, 0.944889},
            { 0.956323, 0.000000, 0.974313, 0.944650, 0.956363, 0.975953, 0.975125, 0.926396, 0.928524, 0.932140},
            {0.972438, 0.974313, 0.000000, 0.970806, 0.977246, 0.873617, 0.727796, 0.972297, 0.971715, 0.968993},
            { 0.933378, 0.944650, 0.970806, 0.000000, 0.938797, 0.972798, 0.972197, 0.941317, 0.942067, 0.940301},
            { 0.602634, 0.956363, 0.977246, 0.938797, 0.000000, 0.981417, 0.979665, 0.957940, 0.958546, 0.947599},
            {0.976144, 0.975953, 0.873617, 0.972798, 0.981417, 0.000000, 0.880709, 0.973661, 0.976154, 0.969450},
            {0.979629, 0.975125, 0.727796, 0.972197, 0.979665, 0.880709, 0.000000, 0.975026, 0.974955, 0.973320},
            {0.952030, 0.926396, 0.972297, 0.941317, 0.957940, 0.973661, 0.975026, 0.000000, 0.428044, 0.938868},
            {0.951910, 0.928524, 0.971715, 0.942067, 0.958546, 0.976154, 0.974955, 0.428044, 0.000000, 0.934823},
            {0.944889, 0.932140, 0.968993, 0.940301, 0.947599, 0.969450, 0.973320, 0.938868, 0.934823, 0.000000}*/

            /*----------------12-Species--------------*/
            //   0           1         2       3         4         5        6           7         8         9
           /* {0.000000, 0.956323, 0.972438, 0.933378, 0.602634, 0.976144, 0.979629, 0.952030, 0.951910, 0.944889, 0.969844, 0.976034},
            { 0.956323, 0.000000, 0.974313, 0.944650, 0.956363, 0.975953, 0.975125, 0.926396, 0.928524, 0.932140, 0.964397, 0.977328},
            {0.972438, 0.974313, 0.000000, 0.970806, 0.977246, 0.873617, 0.727796, 0.972297, 0.971715, 0.968993, 0.981926, 0.657387},
            { 0.933378, 0.944650, 0.970806, 0.000000, 0.938797, 0.972798, 0.972197, 0.941317, 0.942067, 0.940301, 0.967403, 0.973620},
            { 0.602634, 0.956363, 0.977246, 0.938797, 0.000000, 0.981417, 0.979665, 0.957940, 0.958546, 0.947599, 0.976451, 0.980493},
            {0.976144, 0.975953, 0.873617, 0.972798, 0.981417, 0.000000, 0.880709, 0.973661, 0.976154, 0.969450, 0.981696, 0.880203},
            {0.979629, 0.975125, 0.727796, 0.972197, 0.979665, 0.880709, 0.000000, 0.975026, 0.974955, 0.973320, 0.982952, 0.732325},
            {0.952030, 0.926396, 0.972297, 0.941317, 0.957940, 0.973661, 0.975026, 0.000000, 0.428044, 0.938868, 0.968053, 0.975760},
            {0.951910, 0.928524, 0.971715, 0.942067, 0.958546, 0.976154, 0.974955, 0.428044, 0.000000, 0.934823, 0.965112, 0.974737},
            {0.944889, 0.932140, 0.968993, 0.940301, 0.947599, 0.969450, 0.973320, 0.938868, 0.934823, 0.000000, 0.972751, 0.971558},
            {0.969844, 0.964397, 0.981926, 0.967403, 0.976451, 0.981696, 0.982952, 0.968053, 0.965112, 0.972751, 0.000000, 0.980400},
            {0.976034, 0.977328, 0.657387, 0.973620, 0.980493, 0.880203, 0.732325, 0.975760, 0.974737, 0.971558, 0.980400, 0.000000}
*/
            /*----------------14-Species--------------*/
            //  0           1           2       3           4       5       6           7       8           9       10          11      12          13
            {0.000000, 0.956323, 0.972438, 0.933378, 0.602634, 0.976144, 0.979629, 0.952030, 0.951910, 0.944889, 0.969844, 0.976034, 0.981995, 0.977991},
            {0.956323, 0.000000, 0.974313, 0.944650, 0.956363, 0.975953, 0.975125, 0.926396, 0.928524, 0.932140, 0.964397, 0.977328, 0.979132, 0.977075},
            {0.972438, 0.974313, 0.000000, 0.970806, 0.977246, 0.873617, 0.727796, 0.972297, 0.971715, 0.968993, 0.981926, 0.657387, 0.985329, 0.851432},
            {0.933378, 0.944650, 0.970806, 0.000000, 0.938797, 0.972798, 0.972197, 0.941317, 0.942067, 0.940301, 0.967403, 0.973620, 0.978456, 0.973127},
            {0.602634, 0.956363, 0.977246, 0.938797, 0.000000, 0.981417, 0.979665, 0.957940, 0.958546, 0.947599, 0.976451, 0.980493, 0.981616, 0.978559},
            {0.976144, 0.975953, 0.873617, 0.972798, 0.981417, 0.000000, 0.880709, 0.973661, 0.976154, 0.969450, 0.981696, 0.880203, 0.984314, 0.891729},
            {0.979629, 0.975125, 0.727796, 0.972197, 0.979665, 0.880709, 0.000000, 0.975026, 0.974955, 0.973320, 0.982952, 0.732325, 0.986312, 0.857432},
            {0.952030, 0.926396, 0.972297, 0.941317, 0.957940, 0.973661, 0.975026, 0.000000, 0.428044, 0.938868, 0.968053, 0.975760, 0.979947, 0.978015},
            {0.951910, 0.928524, 0.971715, 0.942067, 0.958546, 0.976154, 0.974955, 0.428044, 0.000000, 0.934823, 0.965112, 0.974737, 0.980340, 0.978275},
            {0.944889, 0.932140, 0.968993, 0.940301, 0.947599, 0.969450, 0.973320, 0.938868, 0.934823, 0.000000, 0.972751, 0.971558, 0.977220, 0.975414},
            {0.969844, 0.964397, 0.981926, 0.967403, 0.976451, 0.981696, 0.982952, 0.968053, 0.965112, 0.972751, 0.000000, 0.980400, 0.974406, 0.983850},
            {0.976034, 0.977328, 0.657387, 0.973620, 0.980493, 0.880203, 0.732325, 0.975760, 0.974737, 0.971558, 0.980400, 0.000000, 0.986243, 0.847139},
            {0.981995, 0.979132, 0.985329, 0.978456, 0.981616, 0.984314, 0.986312, 0.979947, 0.980340, 0.977220, 0.974406, 0.986243, 0.000000, 0.987712},
            {0.977991, 0.977075, 0.851432, 0.973127, 0.978559, 0.891729, 0.857432, 0.978015, 0.978275, 0.975414, 0.983850, 0.847139, 0.987712, 0.000000}


            /*Distances for 14-species from ONLINE data source */
           /* {0.000000, 1.598964, 1.783781, 2.307564, 1.516493, 1.817187, 1.917636, 2.005640, 1.939355, 2.004655, 2.166381, 1.691953, 1.755651, 1.731755},
            {1.598964, 0.000000, 1.216141, 1.329463, 1.190023, 1.169735, 1.150314, 1.262927, 1.236149, 1.188648, 1.115359, 1.215328, 1.292810, 1.260670},
            {1.783781, 1.216141, 0.000000, 1.280584, 1.172334, 1.253665, 1.305990, 1.414134, 1.327870, 1.531353, 1.315872, 1.138538, 1.459689, 1.376188},
            {2.307564, 1.329463, 1.280584, 0.000000, 1.643948, 1.150652, 1.207361, 1.226582, 1.275798, 1.547115, 1.534801, 1.474286, 1.555093, 1.613493},
            {1.516493, 1.190023, 1.172334, 1.643948, 0.000000, 1.056982, 1.105590, 0.975917, 1.054940, 0.933174, 1.058689, 0.922420, 1.069952, 1.062297},
            {1.817187, 1.169735, 1.253665, 1.150652, 1.056982, 0.000000, 0.099515, 0.205400, 0.392682, 0.779527, 0.707744, 0.703771, 0.766938, 0.670830},
            {1.917636, 1.150314, 1.305990, 1.207361, 1.105590, 0.099515, 0.000000, 0.273887, 0.401910, 0.687419, 0.719139, 0.716486, 0.850393, 0.688298},
            {2.005640, 1.262927, 1.414134, 1.226582, 0.975917, 0.205400, 0.273887, 0.000000, 0.374749, 0.789727, 0.780987, 0.853308, 0.864829, 0.737118},
            {1.939355, 1.236149, 1.327870, 1.275798, 1.054940, 0.392682, 0.401910, 0.374749, 0.000000, 0.760843, 0.703789, 0.771819, 0.805854, 0.831382},
            {2.004655, 1.188648, 1.531353, 1.547115, 0.933174, 0.779527, 0.687419, 0.789727, 0.760843, 0.000000, 0.714219, 0.611491, 0.631375, 0.569536},
            {2.166381, 1.115359, 1.315872, 1.534801, 1.058689, 0.707744, 0.719139, 0.780987, 0.703789, 0.714219, 0.000000, 0.460883, 0.501793, 0.464598},
            {1.691953, 1.215328, 1.138538, 1.474286, 0.922420, 0.703771, 0.716486, 0.853308, 0.771819, 0.611491, 0.460883, 0.000000, 0.352738, 0.311194},
            {1.755651, 1.292810, 1.459689, 1.555093, 1.069952, 0.766938, 0.850393, 0.864829, 0.805854, 0.631375, 0.501793, 0.352738, 0.000000, 0.280268},
            {1.731755, 1.260670, 1.376188, 1.613493, 1.062297, 0.670830, 0.688298, 0.737118, 0.831382, 0.569536, 0.464598, 0.311194, 0.280268, 0.000000}*/

            /*===============Pairwise Distances=================*/
            //0    1         2       3       4        5       6       7      8         9
           /* {0,     11866,  11765,	11806,	10723,	11923,	11855,	11852,	11870,	11864},
            {11866,  0,     11821,	11941,	11758,	12020,	11887,	11890,	12109,	11935},
            {1765,	11821,  0  ,    11825,	11838,	11209,	9005,	11906,	11830,	11814},
            {11806,	11941,	11825,  0,      11769,	11915,	11830,	11906,	11870,	11900},
            {10723,	11758,	11838,	11769,  0,      11950,	11884,	11930,	11805,	11847},
            {11923,	12020,	11209,	11915,	11950,  0,      11273,	11879,	12028,	11791},
            {11855,	11887,	9005,	11830,	11884,	11273,  0,      11963,	11786,	11910},
            {11852,	11890,	11906,	11906,	11930,	11879,	11963,  0,       11400,	11920},
            {11870,	12109,	11830,	11870,	11805,	12028,	11786,	11400,  0,      11899},
            {11864,	11935,	11814,	11900,	11847,	11791,	11910,	11920,	11899,  0,   }*/



             //   0           1         2       3         4         5        6           7         8         9
            /*{0,     11866,  11765,	11806,	10723,	11923,	11855,	11852,	11870,	11864,	11868,	11865,	11848,	11935,	11747,	11863,	11850,	11943,	11688,	11832},
            {11866,  0,     11821,	11941,	11758,	12020,	11887,	11890,	12109,	11935,	11868,	11930,	11982,	11973,	11970,	11945,	11890,	11963,	11857,	11890},
            {1765,	11821,  0  ,    11825,	11838,	11209,	9005,	11906,	11830,	11814,	11753,	11587,	11877,	11687,	11809,	11600,	11759,	11555,	11714,	11684},
            {11806,	11941,	11825,  0,      11769,	11915,	11830,	11906,	11870,	11900,	11927,	11981,	11829,	11946,	11841,	11899,	11930,	11929,	11692,	11682},
            {10723,	11758,	11838,	11769,  0,      11950,	11884,	11930,	11805,	11847,	11901,	11918,	11874,	11898,	11815,	11948,	11801,	11882,	11709,	11836},
            {11923,	12020,	11209,	11915,	11950,  0,      11273,	11879,	12028,	11791,	11646,	11549,	11905,	11636,	11940,	9548,	11755,	11733,	11851,	11874},
            {11855,	11887,	9005,	11830,	11884,	11273,  0,      11963,	11786,	11910,	11656,	11771,	11856,	11779,	11864,	11345,	11754,	11750,	11747,	11844},
            {11852,	11890,	11906,	11906,	11930,	11879,	11963,  0,       11400,	11920,	11932,	11965,	11936,	11961,	11978,	11894,	11939,	11845,	11889,	12038},
            {11870,	12109,	11830,	11870,	11805,	12028,	11786,	11400,  0,      11899,	11821,	11856,	11861,	12060,	11992,	11877,	11799,	11887,	11882,	11857},
            {11864,	11935,	11814,	11900,	11847,	11791,	11910,	11920,	11899,  0,      11840,	11793,	11533,	10891,	11803,	11842,	11785,	11781,	11697,	11557},
            {11868,	11868,	11753,	11927,	11901,	11646,	11656,	11932,	11821,	11840,  0,      11779,	11720,	11659,	11737,	11677,	11497,	11970,	11745,	11845},
            {11865,	11930,	11587,	11981,	11918,	11549,	11771,	11965,	11856,	11793,	11779,  0,      11888,	11462,	11864,	11741,	11619,	10708,	11780,	11730},
            {11848,	11982,	11877,	11829,	11874,	11905,	11856,	11936,	11861,	11533,	11720,	11888,  0,      11846,	11788,	11714,	11609,	11835,	11678,	11494},
            {11935,	11973,	11687,	11946,	11898,	11636,	11779,	11961,	12060,	10891,	11659,	11462,	11846,  0,      11757,	11772,	10788,	10980,	11794,	11719},
            {11747,	11970,	11809,	11841,	11815,	11940,	11864,	11978,	11992,	11803,	11737,	11864,	11788,	11757,  0,      11760,	11813,	11893,	10971,	11855},
            {11863,	11945,	11600,	11899,	11948,	9548,	11345,	11894,	11877,	11842,	11677,	11741,	11714,	11772,	11760,  0,      11869,	11868,	11622,	11825},
            {11850,	11890,	11759,	11930,	11801,	11755,	11754,	11939,	11799,	11785,	11497,	11619,	11609,	10788,	11813,	11869,  0,      11617,	11654,	11726},
            {11943,	11963,	11555,	11929,	11882,	11733,	11750,	11845,	11887,	11781,	11970,	10708,	11835,	10980,	11893,	11868,	11617,  0,      11710,	11537},
            {11688,	11857,	11714,	11692,	11709,	11851,	11747,	11889,	11882,	11697,	11745,	11780,	11678,	11794,	10971,	11622,	11654,	11710,  0,      11640},
            {11832,	11890,	11684,	11682,	11836,	11874,	11844,	12038,	11857,	11557,	11845,	11730,	11494,	11719,	11855,	11825,	11726,	11537,	11640,  0}*/


    };

    public static void main(String[] args) throws IOException{

        Driver driver=new Driver();
        driver.PrintHeading();
        double antsInPath=0;

        ACO aco=new ACO(basicDistancesMatrix);
        Route bestRoute=null;
        ArrayList<Route> catchRoutes=new ArrayList<Route>();

        int iterator=0;

        do{
        Ants ants=new Ants(aco,NUMBER_OF_ANTS,PHEROMONE_EVAP_FACTOR);
            antsInPath=ants.call();
            if(ants.getRoute()!=null) {
                Route currentRoute = ants.getRoute();
                catchRoutes.add(currentRoute);
                //if (bestRoute == null || currentRoute.getScore() > bestRoute.getScore()) {
                if (bestRoute == null || currentRoute.getScore() > bestRoute.getScore()) {
                    bestRoute = currentRoute;
                    System.out.println(bestRoute.getScore());
                    currentRoute.printRoute(); //Print the route
                }
               // currentRoute.printRoute(); //Print the route
            }
            if(catchRoutes.size()!=0){
                driver.globalUpdatePheromone(aco,bestRoute,catchRoutes);
            }

            System.out.println(antsInPath);
            iterator++;

        } while ( iterator<200); //Check whether last node was arrived 90% percent of ants
        // while (antsInLastNode<(3.5*NUMBER_OF_ANTS) && iterator<20); //Check whether last node was arrived 90% percent of ants

        //System.out.println(antsInLastNode);
        aco.printPheramoneMatrix();

        Newick newick=new Newick(aco,bestRoute.getTaxas());
    }

    public void PrintHeading(){
        int taxasSize=initialRoute.size();
        System.out.println("> "+NUMBER_OF_ANTS+" Artificial Ants ...\n");
        System.out.println("PHERAMONE MATRIX "+taxasSize+" x "+taxasSize);
        for (int x=0;x<180; x++)
            System.out.print("-");
        System.out.println("");
    }

    private void globalUpdatePheromone(ACO aco,Route bestRoute,ArrayList<Route> catchRoutes){
        int taxasSize=initialRoute.size();

        for(int x=0;x<taxasSize;x++) {
            for (int y = 0; y < taxasSize; y++) {
                if(x!=y) {
                    double delta = getDeltaPart(catchRoutes, x, y)/bestRoute.getScore();
                    double oldPValue = aco.getPheramoneMatrix()[x][y];
                    double newValue = (PHEROMONE_EVAP_FACTOR * oldPValue) + (1 - PHEROMONE_EVAP_FACTOR) * delta;
                    if (newValue < 0.00)
                        aco.setPheramoneMatrix(x,y,0);
                    else
                        aco.setPheramoneMatrix(x,y,newValue);
                }
            }
        }
    }

    private double getDeltaPart(ArrayList<Route> catchRoutes,int x,int y) {
        int size = catchRoutes.size();
        int taxasSize = Driver.initialRoute.size();
        double routeScoresSum = 0.0;
        boolean exist = false;
        for (int i = 0; i < size; i++) {
            Route route = catchRoutes.get(i);
            if(route!=null) {
                for (int j = 0; j < route.getTaxas().size(); j++) {
                    int next = j + 1;
                    if (j == route.getTaxas().size() - 1)
                        next = 0;

                    if (route.getTaxas().get(j).getIndex() == x) {
                        if (route.getTaxas().get(next).getIndex() == y) {
                            exist = true;
                            break;
                        }
                    } else if (route.getTaxas().get(j).getIndex() == y) {
                        if (route.getTaxas().get(next).getIndex() == x) {
                            exist = true;
                            break;
                        }
                    }
                }
            }
            if (exist) {
                routeScoresSum+=route.getScore();
                exist=false;
            }
        }
        return routeScoresSum;
    }
}