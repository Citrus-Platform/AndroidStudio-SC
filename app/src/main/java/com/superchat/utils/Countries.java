package com.superchat.utils;


public class Countries {
	private static int inde = 0 ;
		public static enum Country {
			INDIA("India", 91, "IN", inde++),
			EGYPT("Egypt", 20, "EG", inde++),
			IRAN("Iran (Islamic Republic of)", 98, "IR", inde++),
			TURKEY("Turkey", 90, "TR", inde++),
			SAUDI_ARABIA("Saudi Arabia", 966, "SA", inde++),
			SYRIAN_ARAB_REPUBLIC("Syrian Arab Republic", 963, "SY", inde++),
			ISRAEL("Israel", 972, "IL", inde++),
			JORDAN("Jordan", 962, "JO", inde++),
			PALESTINE("Palestine", 970, "PS", inde++),
			LEBANON("Lebanon", 961, "LB", inde++),
			OMAN("Oman", 968, "OM", inde++),
			KUWAIT("Kuwait", 965, "KW", inde++),
			QATAR("Qatar", 974, "QA", inde++),
			BAHRAIN("Bahrain", 973, "BH", inde++),
			CYPRUS("Cyprus", 357, "CY", inde++),						
//			INDIA("India", 91, "IN", inde++),
			PAKISTAN("Pakistan", 92, "PK", inde++),
			MALAYSIA("Malaysia", 60, "MY", inde++),
			BANGLADESH("Bangladesh", 880, "BD", inde++),
			UNITED_ARAB_EMIRATES("United Arab Emirates", 971, "AE", inde++),
			
			AFGHANISTAN("Afghanistan", 93, "AF", inde++),
			ALBANIA("Albania", 355, "AL", inde++),
			ALGERIA("Algeria", 213, "DZ", inde++),
			AMERICAN_SAMOA("American Samoa", 1684, "AS", inde++),
			ANDORRA("Andorra", 376, "AD", inde++),
			ANGOLA("Angola", 244, "AO", inde++),
			ANGUILLA("Anguilla", 1264, "AI", inde++),
			ANTARCTICA("Antarctica", 672, "AQ", inde++),
			ANTIGUA_BARBUDA("Antigua and/or Barbuda", 1268, "AG", inde++), 
			ARGENTINA("Argentina", 54, "AR", inde++), 
			ARMENIA("Armenia", 374, "AM", inde++), 
			ARUBA("Aruba", 297, "AW", inde++), 
			AUSTRALIA("Australia", 61, "AU", inde++), 
			AUSTRIA("Austria", 43, "AT", inde++), 
			AZERBAIJAN("Azerbaijan", 994, "AZ", inde++), 
			BAHAMAS("Bahamas", 1242, "BS", inde++), 			 			 
			BARBADOS("Barbados", 1246, "BB", inde++), 
			BELARUS("Belarus", 375, "BY", inde++), 
			BELGIUM("Belgium", 32, "BE", inde++), 
			BELIZE("Belize", 501, "BZ", inde++), 
			BENIN("Benin", 229, "BJ", inde++), 
			BERMUDA("Bermuda", 1441, "BM", inde++), 
			BHUTAN("Bhutan", 975, "BT", inde++), 
			BOLIVIA("Bolivia", 591, "BO", inde++), 
			BOSNIA_HERZEGOVINA("Bosnia and Herzegovina", 387, "BA", inde++), 
			BOTSWANA("Botswana", 267, "BW", inde++), 
			BRAZIL("Brazil", 55, "BR", inde++), 
			BRITISH_INDIAN_OCEAN_TERRITORY("British lndian Ocean Territory", 246, "IO", inde++), 
			BRITISH_VIRGIN_ISLANDS("British Virgin Islands", 1284, "VG", inde++), 
			BRUNEI_DARUSSALAM("Brunei Darussalam", 673, "BN", inde++), 
			BULGARIA("Bulgaria", 359, "BG", inde++), 
			BURKINA_FASO("Burkina Faso", 226, "BF", inde++), 
			MYANMAR("Myanmar", 95, "MM", inde++), 
			BURUNDI("Burundi", 257, "BI", inde++), 
			CAMBODIA("Cambodia", 855, "KH", inde++), 
			CAMEROON("Cameroon", 237, "CM", inde++), 
			CAPE_VERDE("Cape Verde", 238, "CV", inde++), 
			CAYMAN_ISLANDS("Cayman Islands", 1345, "KY", inde++), 
			CENTRAL_AFRICAN_REPUBLIC("Central African Republic", 236, "CF", inde++), 
			CHAD("Chad", 235, "TD", inde++), 
			CHILE("Chile", 56, "CL", inde++), 
			CHINA("China", 86, "CN", inde++), 
			CHRISTMAS_ISLAND("Christmas Island", 61, "CX", inde++), 
			COCOS_ISLANDS("Cocos (Keeling) Islands", 61, "CC", inde++), 
			COLOMBIA("Colombia", 57, "CO", inde++), 
			COMOROS("Comoros", 269, "KM", inde++), 
			COOK_ISLANDS("Cook Islands", 682, "CK", inde++), 
			COSTA_RICA("Costa Rica", 506, "CR", inde++), 
			CROATIA("Croatia (Hrvatska)", 385, "HR", inde++), 
			CUBA("Cuba", 53, "CU", inde++), 
			 
			CZECH_REPUBLIC("Czech Republic", 420, "CZ", inde++), 
			DEMOCRATIC_REPUBLIC_OF_THE_CONGO("Democratic Republic of the Congo", 243, "CD", inde++), 
			DENMARK("Denmark", 45, "DK", inde++), 
			DJIBOUTI("Djibouti", 253, "DJ", inde++), 
			DOMINICA("Dominica", 1767, "DM", inde++), 
			DOMINICAN_REPUBLIC("Dominican Republic", 1809, "DO", inde++), 
			ECUDAOR("Ecudaor", 593, "EC", inde++), 			
			EL_SALVADOR("El Salvador", 503, "SV", inde++), 
			EQUATORIAL_GUINEA("Equatorial Guinea", 240, "GQ", inde++), 
			ERITREA("Eritrea", 291, "ER", inde++), 
			ESTONIA("Estonia", 372, "EE", inde++), 
			ETHIOPIA("Ethiopia", 251, "ET", inde++), 
			FALKLAND_ISLANDS("Falkland Islands (Malvinas)", 500, "FK", inde++), 
			SOUTH_GEORGIA_SOUTH_SANDWICH_ISLANDS("South Georgia South Sandwich Islands", 500, "GS", inde++),
			FAROE_ISLANDS("Faroe Islands", 298, "FO", inde++), 
			FIJI("Fiji", 679, "FJ", inde++), 
			FINLAND("Finland", 358, "FI", inde++), 
			FRANCE("France", 33, "FR", inde++), 
			FRENCH_POLUNESIA("French Polynesia", 689, "PF", inde++), 
			GABON("Gabon", 241, "GA", inde++), 
			GAMBIA("Gambia", 220, "GM", inde++),  
			GEORGIA("Georgia", 995, "GE", inde++), 
			GERMANY("Germany", 49, "DE", inde++), 
			GHANA("Ghana", 233, "GH", inde++), 
			GIBRALTAR("Gibraltar", 350, "GI", inde++), 
			GREECE("Greece", 30, "GR", inde++), 
			GREENLAND("Greenland", 299, "GL", inde++), 
			GRENADA("Grenada", 1473, "GD", inde++), 
			GUAM("Guam", 1671, "GU", inde++), 
			GUATEMALA("Guatemala", 502, "GT", inde++), 
			GUINEA("Guinea", 224, "GN",inde++), 
			GUINEA_BISSAU("Guinea-Bissau", 245, "GW", inde++), 
			GUYANA("Guyana", 592, "GY", inde++), 
			HAITI("Haiti", 509,"HT", inde++), 
			VATICAN_CITY_STATE("Vatican City State", 39, "VA", inde++), 
			HONDURAS("Honduras", 504, "HN", inde++), 
			HONG_KONG("Hong Kong", 852, "HK", inde++), 
			HUNGARY("Hungary", 36, "HU", inde++), 
			ICELAND("Iceland", 354, "IS", inde++),
			INDONESIA("Indonesia", 62, "ID", inde++),
			IRAQ("Iraq", 964, "IQ", inde++), 
			IRELAND("Ireland", 353, "IE", inde++), 
			ISLE_OF_MAN("Isle of Man", 44, "IM", inde++), 			
			ITALY("Italy", 39, "IT", inde++), 
			IVORY_COAST("Ivory Coast", 225, "CI", inde++), 
			JAMAICA("Jamaica", 1876, "JM", inde++), 
			JAPAN("Japan", 81, "JP", inde++), 
			JERSEY("Jersey", 44, "UK", inde++), 			 
			KAZAKHSTAN("Kazakhstan", 7, "KZ", inde++), 
			KENYA("Kenya", 254, "KE", inde++), 
			KIRIBATI("Kiribati", 686,"KI", inde++), 
			KYRGYZSTAN("Kyrgyzstan", 996, "KG", inde++), 
			LAO_PEOPLE_DEMOCRATIC_REPUBLIC("Laos", 856, "LA", inde++), 
			LATVIA("Latvia", 371, "LV", inde++), 			 
			LESOTHO("Lesotho", 266, "LS", inde++), 
			LIBERIA("Liberia", 231, "LR", inde++), 
			LIBYAN_ARAB_JAMAHIRIYA("Libyan Arab Jamahiriya", 218, "LY", inde++), 
			LIECHTENSTEIN("Liechtenstein", 423, "LI", inde++), 
			LITHUANIA("Lithuania", 370, "LT", inde++), 
			LUXEMBOURG("Luxembourg", 352, "LU", inde++), 
			MACAU("Macau", 853, "MO", inde++), 
			MACEDONIA("Macedonia", 389, "MK", inde++), 
			MADAGASCAR("Madagascar", 261, "MG", inde++), 
			MALAWI("Malawi", 265, "MW", inde++), 
			 
			MALDIVES("Maldives", 960, "MV", inde++), 
			MALI("Mali", 223, "ML", inde++), 
			MALTA("Malta", 356, "MT", inde++), 
			MARSHALL_ISLANDS("Marshall Islands", 692, "MH", inde++), 
			MAURITANIA("Mauritania", 222, "MR", inde++), 
			MAURITIUS("Mauritius", 230, "MU", inde++), 
			REUNION("Reunion", 262, "RE", inde++), 
			MEXICO("Mexico", 52, "MX", inde++), 
			MICRONESIA("Micronesia", 691, "FM", inde++), 
			MOLDOVA("Moldova", 373, "MD", inde++), 
			MONACO("Monaco", 377, "MC", inde++), 
			MONGOLIA("Mongolia", 976, "MN", inde++), 
			MONTENEGRO("Montenegro", 382, "ME", inde++), 
			MONTSERRAT("Montserrat", 1664, "MS", inde++), 
			MOROCCO("Morocco", 212, "MA", inde++), 
			WESTERN_SAHARA("Western Sahara", 212, "EH", inde++), 
			MOZAMBIQUE("Mozambique", 258, "MZ", inde++), 
			NAMIBIA("Namibia", 264, "NA", inde++), 
			NAURU("Nauru", 674, "NR", inde++), 
			NEPAL("Nepal", 977, "NP", inde++), 
			NETHERLANDS("Netherlands", 31, "NL", inde++), 
			NEW_CALEDONIA("New Caledonia", 687, "NC", inde++), 
			NEW_ZEALAND("New Zealand", 64, "NZ", inde++), 
			NICARAGUA("Nicaragua", 505, "NI", inde++), 
			NIGER("Niger", 227, "NE", inde++), 
			NIGERIA("Nigeria", 234, "NG", inde++), 
			NIUE("Niue", 683, "NU", inde++), 
			NORFORK_ISLAND("Norfork Island", 672, "NF", inde++), 
			NORTHERN_MARIANA_ISLANDS("Northern Mariana Islands", 1670, "MP", inde++), 
			NORWAY("Norway", 47, "NO", inde++), 			 			 
			PALAU("Palau", 680, "PW", inde++), 
			PANAMA("Panama", 507,"PA", inde++), 
			PAPUA_NEW_GUINEA("Papua New Guinea", 675, "PG", inde++), 
			PARAGUAY("Paraguay", 595, "PY", inde++), 
			PERU("Peru", 51, "PE", inde++), 
			PHILIPPINES("Philippines", 63, "PH", inde++), 
			PITCAIRN("Pitcairn", 870, "PN", inde++), 
			POLAND("Poland", 48, "PL", inde++), 
			PORTUGAL("Portugal", 351, "PT", inde++), 
			PUERTO_RICO("Puerto Rico", 1787, "PR", inde++), 			
			ROMANIA("Romania", 40, "RO", inde++), 
			RUSSIAN_FEDERATION("Russian Federation", 7, "RU", inde++), 
			RWANDA("Rwanda", 250, "RW", inde++), 
			ST_HELENA("St. Helena", 290, "SH", inde++), 
			SAINT_BARTHELEMY("Saint Barthelemy", 590, "GP", inde++), 
			SAINT_KITTS_AND_NEVIS("Saint Kitts and Nevis", 1869, "KN", inde++), 
			SAINT_LUCIA("Saint Lucia", 1758, "LC", inde++), 
			SAINT_MARTIN("Saint Martin", 1599, "MF", inde++), 
			ST_PIERRE_AND_MIQUELON("St. Pierre and Miquelon", 508, "PM", inde++), 
			SAINT_VINCENT_AND_THE_GRENADINES("Saint Vincent and the Grenadines", 1784, "VC", inde++), 
			SAMOA("Samoa", 685, "WS", inde++), 
			SAN_MARINO("San Marino", 378, "SM", inde++), 
			SAO_TOME_AND_PRINCIPE("Sao Tome and Principe", 239, "ST", inde++), 			
			SENEGAL("Senegal", 221, "SN", inde++), 
			SERBIA("Serbia", 381, "RS", inde++), 
			SEYCHELLES("Seychelles", 248, "SC", inde++), 
			SIERRA_LEONE("Sierra Leone", 232, "SL", inde++), 
			SINGAPORE("Singapore", 65, "SG", inde++), 
			SLOVAKIA("Slovakia", 421, "SK", inde++), 
			SLOVENIA("Slovenia", 386, "SI", inde++), 
			SOLOMON_ISLANDS("Solomon Islands", 677, "SB", inde++), 
			SOMALIA("Somalia", 252, "SO", inde++), 
			SOUTH_AFRICA("South Africa", 27, "ZA", inde++), 
			KOREA("South Korea", 82, "KR", inde++), 
			SPAIN("Spain", 34, "ES", inde++), 
			SRI_LANKA("Sri Lanka", 94, "LK", inde++), 
			SUDAN("Sudan", 249, "SD", inde++), 
			SURINAME("Suriname", 597, "SR", inde++), 
			SVALBARN_AND_JAN_MAYEN_ISLANDS("Svalbarn and Jan Mayen Islands", 47, "NO", inde++), 
			SWAZILAND("Swaziland", 268, "SZ", inde++), 
			SWEDEN("Sweden", 46, "SE", inde++), 
			SWITZERLAND("Switzerland", 41, "CH", inde++), 			 
			TAIWAN("Taiwan", 886, "TW" ,inde++), 
			TAJIKISTAN("Tajikistan", 992, "TJ", inde++), 
			TANZANIA("Tanzania", 255, "TZ", inde++), 
			THAILAND("Thailand", 66, "TH", inde++), 
			TIMOR_LESTE("Timor-Leste", 670, "TL", inde++), 
			EAST_TIMOR("East Timor", 670, "TL", inde++), 
			TOGO("Togo", 228, "TG", inde++), 
			TOKELAU("Tokelau", 690, "TK", inde++), 
			TONGA("Tonga", 676, "TO", inde++), 
			TRINIDAD_AND_TOBAGO("Trinidad and Tobago", 1868, "TT", inde++), 
			TUNISIA("Tunisia", 216, "TN", inde++), 			 
			TURKMENISTAN("Turkmenistan", 993, "TM", inde++), 
			TURKS_AND_CAICOS_ISLANDS("Turks and Caicos Islands", 1649, "TC", inde++), 
			TUVALU("Tuvalu", 668, "TV", inde++), 
			UGANDA("Uganda", 256, "UG", inde++), 
			UKRAINE("Ukraine", 380, "UA", inde++), 			 
			UNITED_KINGDOM("United Kingdom", 44, "UK", inde++), 
			UNITED_STATES("United States", 1, "US", inde++), 
			CANADA("Canada", 1, "CA", inde++), 
			URUGUAY("Uruguay", 598, "UY", inde++), 
			VIRGIN_ISLANDS("Virgin Islands (U.S.)", 1340, "VI", inde++), 
			UZBEKISTAN("Uzbekistan", 998, "UZ", inde++), 
			VANUATU("Vanuatu", 678, "VU", inde++), 
			VENEZUELA("Venezuela", 58, "VE", inde++), 
			VIETNAM("Vietnam", 84, "VN", inde++), 
			WALLIS_AND_FUTUNA_ISLANDS("Wallis and Futuna Islands", 681, "WF", inde++), 
			YEMEN("Yemen", 967, "YE", inde++), 
			ZAIRE("Zaire", 243, "CD", inde++), 
			ZAMBIA("Zambia", 260, "ZM", inde++), 
			ZIMBABWE("Zimbabwe", 263, "ZW", inde++), 
			CONGO("Congo", 242, "CG", inde++), 
			KOREA_DEMOCRATIC("North Korea", 850, "KP", inde++), 
			MARTINIQUE("Martinique", 596, "MQ", inde++), 
			FRENCH_GUIANA("French Guiana", 594, "GF", inde++), 
			//FRENCH_SOUTHERN_TERRITORIES("French Southern Territories", ,inde++), 
			GUADELOUPE("Guadeloupe", 502, "GT", inde++), 
			//("Virigan Islands (British)", ,inde++), ("Bouvet Island", ,inde++), ("Heard and Mc Donald Islands", )
			;

			private String name;
			private int code;
			private String countryCode;
			private int index;

			private Country(String name, int code) {
				this.name = name;
				this.code = code;
			}
			private Country(String name, int code,int index) {
				this.name = name;
				this.code = code;
				this.index = index;
			}
			private Country(String name, int code, String countryCode, int index) {
				this.name = name;
				this.code = code;
				this.countryCode = countryCode;
				this.index = index;
			}

			public String getStationName() {
				return name;
			}

			public int getCode() {
				return code;
			}
			public String getCountryCode() {
				return countryCode;
			}
			public int getIndex() {
				return index;
			}

			public String toString() {
				return name;
			}
		}
		
		
		public static Countries.Country getCountry(String country) {
			Countries.Country[] c = Countries.Country.values();
			for (Countries.Country cnty : c) {
				if (cnty.getStationName().equalsIgnoreCase(country)) {
					return cnty;
				}
			}
			return null;
		}
		public static Countries.Country getCountryName(String cc) {
			Countries.Country[] c = Countries.Country.values();
			for (Countries.Country cnty : c) {
				if (cnty.getCountryCode() != null && cnty.getCountryCode().equalsIgnoreCase(cc)) {
					return cnty;
				}
			}
			return null;
		}
		public static Countries.Country getCountryNameViaCode(String code) {
			Countries.Country[] c = Countries.Country.values();
			for (Countries.Country cnty : c) {
				if (String.valueOf(cnty.getCode()).equalsIgnoreCase(code)) {
					return cnty;
				}
			}
			return null;
		}
		public static Countries.Country getCountryName1(String cc) {
			Countries.Country[] c = Countries.Country.values();
			for (Countries.Country cnty : c) {
				if ((""+cnty.code).equals(cc)) {
					return cnty;
				}
			}
			return null;
		}
		public static Countries.Country getCodeValue(String cc) {
			Countries.Country[] c = Countries.Country.values();
			for (Countries.Country cnty : c) {
				if ((""+cnty.countryCode).equalsIgnoreCase(cc)) {
					return cnty;
				}
			}
			return c[0];
		}
	}	
	