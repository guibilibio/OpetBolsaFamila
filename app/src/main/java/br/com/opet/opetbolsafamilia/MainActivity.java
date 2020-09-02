package br.com.opet.opetbolsafamilia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    EditText inputCodigo;
    EditText inputAno;
    TextView output;
    List<BolsaFamilia> bolsas;

    String key = "Chave do Token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bolsas = new ArrayList<>();
        setContentView(R.layout.activity_main);

        inputCodigo = findViewById(R.id.inputCodigoMunicipio);
        inputAno = findViewById(R.id.inputAno);
        output = findViewById(R.id.output);

    }

    public void findBolsa(View view) {
        bolsas.clear();
        RequestQueue queue = Volley.newRequestQueue(this);

        String codigo = inputCodigo.getText().toString();
        String ano = inputAno.getText().toString();

        if (codigo.length() != 7 || ano.length() != 4) {
            output.setText("Voce deve informar código valido e um ano valido (YYYY)");
            return;
        } else {
            output.setText("Executando ...");
        }

        for (int i = 1; i <= 12; i++) {
            String mes = i < 10 ? "0" + i : "" + i;
            String url = "http://www.transparencia.gov.br/api-de-dados/bolsa-familia-por-municipio?mesAno=" + ano + mes + "&codigoIbge=" + codigo + "&pagina=1";

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONObject result = response.getJSONObject(0);
                            if (result != null) {
                                JSONObject municipio_obj = result.getJSONObject("municipio");

                                BolsaFamilia b = new BolsaFamilia();
                                b.setMunicipioName(municipio_obj.getString("nomeIBGE"));
                                b.setEstadoSigla(municipio_obj.getJSONObject("uf").getString("sigla"));
                                b.setEstado(municipio_obj.getJSONObject("uf").getString("nome"));
                                b.setBeneficiarios(result.getInt("quantidadeBeneficiados"));
                                b.setTotalPago(result.getDouble("valor"));
                                bolsas.add(b);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> output.setText("Erro ao acessar transparencia.gov.br")
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("chave-api-dados", key);
                    headers.put("Accept", "*/*");
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    120 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(request);
        }

        queue.addRequestFinishedListener(request -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Nome: " + bolsas.get(0).getMunicipioName()).append("\n");
            sb.append("Estado: " + bolsas.get(0).getEstado() + " | " + bolsas.get(0).getEstadoSigla()).append("\n");
            sb.append("Beneficiarios: " + bolsas.get(0).getBeneficiarios()).append("\n");
            sb.append("Total Pago no Mês: R$ " + formatCurrency(bolsas.get(0).getTotalPago())).append("\n").append("\n");

            AtomicInteger bene = new AtomicInteger();
            AtomicReference<Double> teste = new AtomicReference<>((double) 0);

            bolsas.forEach(b -> {
                teste.updateAndGet(v -> v + b.getTotalPago());
                if (b.getBeneficiarios() > bene.get()) {
                    bene.set(b.getBeneficiarios());
                }
            });

            sb.append("Maior quantia de beneficiarios: " + bene.get()).append("\n");
            sb.append("Média Mês: (" + bolsas.size() + ") R$ " + formatCurrency(teste.get() / bolsas.size())).append("\n");
            sb.append("Queue: " +  bolsas.size() + "/12").append("\n");
            output.setText(sb.toString());
        });

    }

    public static String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(amount);
    }

}