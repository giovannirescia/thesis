#!/bin/bash

AV_CORES=2
PROBLEM_FOLDER=tests
procs=0

### Las líneas #SBATCH configuran los recursos de la tarea
### (aunque parezcan estar comentadas)

### Nombre de la tarea
#SBATCH --job-name=nombre

### Procesos totales
#SBATCH --ntasks=1

### Cores por proceso (OpenMP/Pthreads/etc)
### Recordar exportar OMP_NUM_THREADS/MKL_NUM_THREADS/etc con el mismo valor
#SBATCH --cpus-per-task=1
export OMP_NUM_THREADS=1

### Tiempo de ejecucion. Formato dias-horas:minutos. Maximo una semana.
#SBATCH --time 7-0:00

### Environment setup
. /etc/profile

### Environment modules
module load compilers/intel

### Ejecutar la tarea
### NOTA: srun configura MVAPICH2 y MPICH con lo puesto arriba,
###       no hay que llamar a mpirun.
srun algun_programa


# FD 3 will be tied to a named pipe.
mkfifo pipe; exec 3<>pipe


# This is the job we're running.
doTask() {
    PID=$BASHPID
    echo "proccessing file: "$1
    echo "proccess: "$PID
    echo "proccess: "$PID " - file: " $1  >> $PID.tmp
    echo >&3
}

files=($(find $PROBLEM_FOLDER -type f))
fileid=0
nfiles=${#files[@]}

 # Start off with $AV_CORES instances of it.
 # Each time an instance terminates, write a newline to the named pipe.
while([ $procs -lt $AV_CORES ])
do
    if [ $fileid -lt $nfiles ]
    then
		doTask ${files[$fileid]} &
		let procs+=1
		let fileid+=1
    else
		break
    fi
done  

 # Each time we get a line from the named pipe, launch another job.
while read; do
    if [ $fileid -lt $nfiles ]
    then
		doTask ${files[$fileid]} &
		let fileid+=1
    else
		break
    fi
done <&3
wait
rm -f pipe

cat *.tmp >> log.out
#rm  *.tmp
echo "DONE"
