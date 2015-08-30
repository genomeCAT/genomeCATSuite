package org.molgen.genomeCATPro.common;


/** * @(#)LowessAlgrithm.java * * Copyright (c) 2004 by Wei Chen
  * * @author Wei Chen
  * * Email: wei@molgen.mpg.de
  * * This program is developed based on the algorithm by William S. Cleveland
  * * This program is free software; you can redistribute it and/or
  * * modify it under the terms of the GNU General Public License 
  * * as published by the Free Software Foundation; either version 2 
  * * of the License, or (at your option) any later version, 
  * * provided that any use properly credits the author. 
  * * This program is distributed in the hope that it will be useful,
  * * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  * * GNU General Public License for more details at http://www.gnu.org * * */

import java.io.PrintStream;
import java.util.Arrays;

/**
 * Class LowessAlgoritm is a class implementing the Lowess Algorithm.Lowess requires that the arrays x and y,
 * which are the horizontal and vertical coordinates respectively,of the scatterplot,  be such that x is 
 * sorted from the smallest to largest.
 **/


public class LowessAlgorithm
{

    public LowessAlgorithm()
    {
    }
    /**
     * Calculate the smoothing value.
     *@param x[] the array of x coordinates
     *@param y[] the array of y coordinates
     *@n the size of input array
     *@f the fraction of the total values as a window, window size = f*n
     *@nsteps the repeat numbers
     *@delta Nonnegative parameter to save the computation. If delta>0, the smoothing value will be calculated 
             for the sequences, x[1], x[delta+1],x[2*delta+1]...
     *@return the smoothing value.
     **/
    public static Object[] lowess(float x[], float y[], int n, float f, int nsteps, float delta){
        boolean ok = false;
        int data_len = x.length;
        float y_fit[] = new float[data_len];
        float rob_weight[] = new float[data_len];
        float residual[] = new float[data_len];
        for(int z = 0; z < data_len; z++)
        {
            y_fit[z] = 0.0F;
            rob_weight[z] = 0.0F;
            residual[z] = 0.0F;
        }

        if(n < 2)
        {
            y_fit[0] = y[0];
            return (new Object[] {
                y_fit, rob_weight, residual
            });
        }
        int ns = Math.max(2, Math.min(n, (int)((double)(f * (float)n) + 9.9999999999999995E-08D))) - 1;
        for(int iterations = 1; iterations <= nsteps + 1; iterations++)
        {
            int nleft = 0;
            int nright = ns;
            int last_index = -1;
            int i = 0;
            do
            {
label0:
                {
                    do
                    {
                        if(nright >= n - 1)
                            break label0;
                        float d1 = x[i] - x[nleft];
                        float d2 = x[nright + 1] - x[i];
                        if(d1 <= d2)
                            break;
                        nleft++;
                        nright++;
                    } while(true);
                }
                Object dummyResult[] = lowest(x, y, n, x[i], nleft, nright, residual, iterations > 1, rob_weight);
                y_fit[i] = ((Double)dummyResult[0]).floatValue();
                residual = (float[])dummyResult[1];
                ok = ((Boolean)dummyResult[2]).booleanValue();
                if(!ok)
                    y_fit[i] = y[i];
                if(last_index < i - 1)
                {
                    float denom = x[i] - x[last_index];
                    for(int j = last_index + 1; j < i; j++)
                    {
                        float alpha = (x[j] - x[last_index]) / denom;
                        y_fit[j] = alpha * y_fit[i] + (1.0F - alpha) * y_fit[last_index];
                    }

                }
                last_index = i;
                float cut = x[last_index] + delta;
                for(i = last_index + 1; i <= n - 1; i++)
                {
                    if(x[i] > cut)
                        break;
                    if(x[i] == x[last_index])
                    {
                        y_fit[i] = y_fit[last_index];
                        last_index = i;
                    }
                }

                i = Math.max(last_index + 1, i - 1);
            } while(last_index < n - 1);
            for(i = 0; i < n; i++)
                residual[i] = y[i] - y_fit[i];

            if(iterations > nsteps)
                break;
            for(i = 0; i < n; i++)
                rob_weight[i] = Math.abs(residual[i]);

            int m1 = n / 2;
            Arrays.sort(rob_weight, 0, n);
            float cmad;
            if(n % 2 == 0)
            {
                int m2 = n - m1 - 1;
                cmad = 3F * (rob_weight[m1] + rob_weight[m2]);
            } else
            {
                cmad = 6F * rob_weight[m1];
            }
            float c9 = 0.999F * cmad;
            float c1 = 0.001F * cmad;
            for(i = 0; i < n; i++)
            {
                float r = Math.abs(residual[i]);
                if(r <= c1)
                {
                    rob_weight[i] = 1.0F;
                } else
                {
                    if(r <= c9)
                        rob_weight[i] = power2(1.0F - power2(r / cmad));
                    else
                        rob_weight[i] = 0.0F;
                }
            }

        }

        return (new Object[] {
            y_fit, rob_weight, residual
        });
    }

   
   

    static Object[] lowest(float x[], float y[], int n, float xs, int nleft, int nright, float w[], boolean userw, 
			   float rw[]) {
        float ys = 0.0F;
        boolean ok = false;
        float range = x[n - 1] - x[0];
        float h = Math.max(xs - x[nleft], x[nright] - xs);
        float h9 = 0.999F * h;
        float h1 = 0.001F * h;
        float a = 0.0F;
        int j;
        for(j = nleft; j <= n - 1; j++)
        {
            w[j] = 0.0F;
            float r = Math.abs(x[j] - xs);
            if(r <= h9)
            {
                if(r <= h1)
                    w[j] = 1.0F;
                else
                    w[j] = power3(1.0F - power3(r / h));
                if(userw)
                    w[j] *= rw[j];
                a += w[j];
                continue;
            }
            if(x[j] > xs)
                break;
        }

        int nrt = j - 1;
        if(a <= 0.0F)
        {
            ok = false;
        } else
        {
            ok = true;
            for(j = nleft; j <= nrt; j++)
                w[j] /= a;

            if(h > 0.0F)
            {
                a = 0.0F;
                for(j = nleft; j <= nrt; j++)
                    a += w[j] * x[j];

                float b = xs - a;
                float c = 0.0F;
                for(j = nleft; j <= nrt; j++)
                    c += w[j] * power2(x[j] - a);

                if(Math.sqrt(c) > (double)(0.001F * range))
                {
                    b /= c;
                    for(j = nleft; j <= nrt; j++)
                        w[j] *= b * (x[j] - a) + 1.0F;

                }
            }
            ys = 0.0F;
            for(j = nleft; j <= nrt; j++)
                ys += w[j] * y[j];

        }
        return (new Object[] {
            new Double(ys), w, new Boolean(ok)
        });
    }

    public static void main(String args[])
    {
        testDriver();
    }

    static float power2(float x)
    {
        return (float)Math.pow(x, 2D);
    }

    static float power3(float x)
    {
        return (float)Math.pow(x, 3D);
    }

    private static void testDriver()
    {
        float xin[] = {
            1.0F, 2.0F, 3F, 4F, 5F, 6F, 6F, 6F, 6F, 6F, 
            6F, 6F, 6F, 6F, 6F, 8F, 10F, 12F, 14F, 50F
        };
        float yin[] = {
            18F, 2.0F, 15F, 6F, 10F, 4F, 16F, 11F, 7F, 3F, 
            14F, 17F, 20F, 12F, 9F, 13F, 1.0F, 8F, 5F, 19F
        };
        Object result[] = lowess(xin, yin, xin.length, 0.25F, 0, 0.0F);
        float ys[] = (float[])result[0];
        System.out.println("[LowessAlgorithm::testDriver -> f=0.25, nsteps=0, delta=0.0] ys:");
        for(int i = 0; i < ys.length; i++)
            System.out.println(" " + ys[i]);

        result = lowess(xin, yin, xin.length, 0.25F, 0, 3F);
        ys = (float[])result[0];
        System.out.println("[LowessAlgorithm::testDriver -> f=0.25, nsteps=0, delta=3.0] ys:");
        for(int i = 0; i < ys.length; i++)
            System.out.println(" " + ys[i]);

        result = lowess(xin, yin, xin.length, 0.25F, 2, 0.0F);
        ys = (float[])result[0];
        System.out.println("[LowessAlgorithm::testDriver -> f=0.25, nsteps=2, delta=0.0] ys:");
        for(int i = 0; i < ys.length; i++)
            System.out.println(" " + ys[i]);

    }
}
